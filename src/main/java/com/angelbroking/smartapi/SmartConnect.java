package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.utils.Utils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Objects;

import static com.angelbroking.smartapi.utils.Constants.*;

@Slf4j
public class SmartConnect {
    public static SessionExpiryHook sessionExpiryHook = null;
    public static boolean ENABLE_LOGGING = false;
    private Proxy proxy = null;
    @Setter
    private String apiKey;
    private String accessToken;
    private String refreshToken;

    private String feedToken;
    private Routes routes = new Routes();
    private String userId;
    private SmartAPIRequestHandler smartAPIRequestHandler;

    public SmartConnect() {
    }

    public SmartConnect(String apiKey) {
        this.apiKey = apiKey;
    }

    public SmartConnect(String apiKey, String accessToken, String refreshToken) {
        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Registers callback for session error.
     *
     * @param hook can be set to get callback when session is expired.
     */
    public void setSessionExpiryHook(SessionExpiryHook hook) {
        sessionExpiryHook = hook;
    }

    /**
     * Returns apiKey of the App.
     *
     * @return String apiKey is returned.
     * @throws NullPointerException if _apiKey is not found.
     */
    public String getApiKey() throws NullPointerException {
        if (Objects.nonNull(apiKey)) return apiKey;
        throw new NullPointerException();
    }

    /**
     * Returns accessToken.
     *
     * @return String access_token is returned.
     * @throws NullPointerException if accessToken is null.
     */
    public String getAccessToken() throws NullPointerException {
        if (Objects.nonNull(accessToken)) return accessToken;
        throw new NullPointerException();
    }

    public String getFeedToken() throws NullPointerException {
        if (Objects.nonNull(feedToken)) return feedToken;
        throw new NullPointerException();
    }

    /**
     * Returns userId.
     *
     * @return String userId is returned.
     * @throws NullPointerException if userId is null.
     */
    public String getUserId() throws NullPointerException {
        if (Objects.nonNull(userId)) return userId;
        throw new NullPointerException();
    }

    /**
     * Set userId.
     *
     * @param id is user_id.
     */
    public void setUserId(String id) {
        userId = id;
    }

    /**
     * Returns publicToken.
     *
     * @return String public token is returned.
     * @throws NullPointerException if publicToken is null.
     */
    public String getPublicToken() throws NullPointerException {
        if (Objects.nonNull(refreshToken)) return refreshToken;
        throw new NullPointerException();
    }

    /**
     * Set the accessToken received after a successful authentication.
     *
     * @param accessToken is the access token received after sending request token
     *                    and api secret.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Set publicToken.
     *
     * @param publicToken is the public token received after sending request token
     *                    and api secret.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Retrieves login url
     *
     * @return String loginUrl is returned.
     */
    public String getLoginURL() throws NullPointerException {
        String baseUrl = routes.getLoginUrl();
        return baseUrl;
    }

    /**
     * API to generate session using client credentials
     *
     * @param clientCode
     * @param password
     * @param totp
     * @return
     * @throws SmartAPIException
     * @throws IOException
     */
    public User generateSession(String clientCode, String password, String totp) throws Exception {
        smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);
        User user;
        // Create JSON params object needed to be sent to api.
        JSONObject params = new JSONObject();
        params.put("clientcode", clientCode);
        params.put("password", password);
        params.put("totp", totp);

        JSONObject loginResultObject = smartAPIRequestHandler.postRequest(this.apiKey, routes.getLoginUrl(),
                params);
        log.info("login result: {}", loginResultObject);
        String jwtToken = loginResultObject.getJSONObject("data").getString("jwtToken");
        String refreshToken = loginResultObject.getJSONObject("data").getString("refreshToken");
        String feedToken = loginResultObject.getJSONObject("data").getString("feedToken");
        this.feedToken = feedToken;
        String url = routes.get("api.user.profile");
        user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
        user.setAccessToken(jwtToken);
        user.setRefreshToken(refreshToken);
        user.setFeedToken(feedToken);
        return user;
    }

    /**
     * Get a new access token using refresh token.
     *
     * @param refreshToken is the refresh token obtained after generateSession.
     * @param apiSecret    is unique for each app.
     * @return TokenSet contains user id, refresh token, api secret.
     */
    public TokenSet renewAccessToken(String accessToken, String refreshToken) {
        TokenSet tokenSet = null;
        try {
            String hashableText = this.apiKey + refreshToken + accessToken;
            String sha256hex = sha256Hex(hashableText);

            JSONObject params = new JSONObject();
            params.put("refreshToken", refreshToken);
            params.put("checksum", sha256hex);
            String url = routes.get("api.refresh");
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

            accessToken = response.getJSONObject("data").getString("jwtToken");
            refreshToken = response.getJSONObject("data").getString("refreshToken");

            tokenSet = new TokenSet();
            tokenSet.setUserId(userId);
            tokenSet.setAccessToken(accessToken);
            tokenSet.setRefreshToken(refreshToken);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return tokenSet;
    }

    /**
     * Hex encodes sha256 output for android support.
     *
     * @param str is the String that has to be encrypted.
     * @return Hex encoded String.
     */
    public String sha256Hex(String str) {
        byte[] a = DigestUtils.sha256(str);
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Get the profile details of the use.
     *
     * @return Profile is a POJO which contains profile related data.
     */
    public User getProfile() throws Exception {
        User user = null;
        String url = routes.get("api.user.profile");
        user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken));
        return user;
    }

    /**
     * Places an order.
     *
     * @param orderParams is Order params.
     * @param variety     variety="regular". Order variety can be bo, co, amo,
     *                    regular.
     * @return Order contains only orderId.
     */
    public Order placeOrder(OrderParams orderParams, String variety) throws Exception {
        Order order = null;
        String url = routes.get("api.order.place");
        JSONObject params = new JSONObject();
        if (orderParams.getExchange() != null)
            params.put("exchange", orderParams.getExchange());
        if (orderParams.getTradingsymbol() != null)
            params.put("tradingsymbol", orderParams.getTradingsymbol());
        if (orderParams.getTransactiontype() != null)
            params.put("transactiontype", orderParams.getTransactiontype());
        if (orderParams.getQuantity() != null)
            params.put("quantity", orderParams.getQuantity());
        if (orderParams.getPrice() != null)
            params.put("price", orderParams.getPrice());
        if (orderParams.getProducttype() != null)
            params.put("producttype", orderParams.getProducttype());
        if (orderParams.getOrdertype() != null)
            params.put("ordertype", orderParams.getOrdertype());
        if (orderParams.getDuration() != null)
            params.put("duration", orderParams.getDuration());
        if (orderParams.getPrice() != null)
            params.put("price", orderParams.getPrice());
        if (orderParams.getSymboltoken() != null)
            params.put("symboltoken", orderParams.getSymboltoken());
        if (orderParams.getSquareoff() != null)
            params.put("squareoff", orderParams.getSquareoff());
        if (orderParams.getStoploss() != null)
            params.put("stoploss", orderParams.getStoploss());
        if (orderParams.getTriggerprice() != null)
            params.put("triggerprice", orderParams.getTriggerprice());

        params.put("variety", variety);
        JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(jsonObject)) {
            order = Utils.convertJsonObject(jsonObject.getJSONObject("data"), Order.class);
        }
        log.info("order : {}", order);
        return order;
    }

    /**
     * Modifies an open order.
     *
     * @param orderParams is Order params.
     * @param variety     variety="regular". Order variety can be bo, co, amo,
     *                    regular.
     * @param orderId     order id of the order being modified.
     * @return Order object contains only orderId.
     */
    public Order modifyOrder(String orderId, OrderParams orderParams, String variety) throws Exception {
        Order order = null;
        String url = routes.get("api.order.modify");
        JSONObject params = new JSONObject();
        if (orderParams.getExchange() != null)
            params.put("exchange", orderParams.getExchange());
        if (orderParams.getTradingsymbol() != null)
            params.put("tradingsymbol", orderParams.getTradingsymbol());
        if (orderParams.getSymboltoken() != null)
            params.put("symboltoken", orderParams.getSymboltoken());
        if (orderParams.getQuantity() != null)
            params.put("quantity", orderParams.getQuantity());
        if (orderParams.getPrice() != null)
            params.put("price", orderParams.getPrice());
        if (orderParams.getProducttype() != null)
            params.put("producttype", orderParams.getProducttype());
        if (orderParams.getOrdertype() != null)
            params.put("ordertype", orderParams.getOrdertype());
        if (orderParams.getDuration() != null)
            params.put("duration", orderParams.getDuration());

        params.put("variety", variety);
        params.put("orderid", orderId);
        JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(jsonObject)) {
            order = Utils.convertJsonObject(jsonObject.getJSONObject("data"), Order.class);
        }
        return order;
    }

    /**
     * Cancels an order.
     *
     * @param orderId order id of the order to be cancelled.
     * @param variety [variety="regular"]. Order variety can be bo, co, amo,
     *                regular.
     * @return Order object contains only orderId.
     */
    public Order cancelOrder(String orderId, String variety) throws Exception {
        Order order = null;
        String url = routes.get("api.order.cancel");
        JSONObject params = new JSONObject();
        params.put("variety", variety);
        params.put("orderid", orderId);

        JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(jsonObject)) {
            order = Utils.convertJsonObject(jsonObject.getJSONObject("data"), Order.class);
        }
        return order;
    }

    /**
     * Returns list of different stages an order has gone through.
     *
     * @param orderId is the order id which is obtained from orderbook.
     * @return List of multiple stages an order has gone through in the system.
     * @throws SmartAPIException is thrown for all Smart API trade related errors.
     */
    @SuppressWarnings({})
    public JSONObject getOrderHistory(String clientId) {
        JSONObject response = null;
        try {
            String url = routes.get("api.order.book");
            response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            log.info("Order history : {}", response);
        } catch (Exception e) {
            log.error("Exception#: {}", e.getMessage());
            return null;
        }
        return response;
    }

    /**
     * Retrieves last price. User can either pass exchange with tradingsymbol or
     * instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265,
     * 265}.
     *
     * @return Map of String and LTPQuote.
     */
    public JSONObject getLTP(String exchange, String tradingSymbol, String symboltoken) throws Exception {
        JSONObject params = new JSONObject();
        params.put("exchange", exchange);
        params.put("tradingsymbol", tradingSymbol);
        params.put("symboltoken", symboltoken);

        String url = routes.get("api.ltp.data");
        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

        return response.isNull("data") ? null : response.getJSONObject("data");
    }

    /**
     * Retrieves list of trades executed.
     *
     * @return List of trades.
     */
    public List<Order> getTradeBook() {
        List<Order> response = null;
        try {
            String url = routes.get("api.order.trade.book");
            JSONObject apiResponse = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            if (Objects.nonNull(apiResponse)) {
                response = Utils.convertJsonArrayToList(apiResponse.getJSONArray("data"), Order.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return response;
    }

    /**
     * Retrieves RMS.
     *
     * @return Object of RMS.
     * @throws SmartAPIException is thrown for all Smart API trade related errors.
     * @throws JSONException     is thrown when there is exception while parsing
     *                           response.
     * @throws IOException       is thrown when there is connection error.
     */
    public JSONObject getRMS() throws Exception {
        String url = routes.get("api.order.rms.data");
        JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
        return response.isNull("data") ? null : response.getJSONObject("data");
    }

    /**
     * Retrieves Holding.
     *
     * @return Object of Holding.
     */
    public JSONArray getHolding() throws Exception {
        JSONObject response = null;
        String url = routes.get("api.order.rms.holding");
        response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
        return response.getJSONArray("data");
    }


    /**
     * Retrieves All Holdings.
     *
     * @return Object of Holding.
     */
    public JSONObject getAllHolding() throws Exception {
        try {
            String url = routes.get("api.order.rms.AllHolding");
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return response.isNull("data") ? null : response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while getting all holdings {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in getting all holdings %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while getting all holdings {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in getting all holdings %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while getting all holdings {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in getting all holdings %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    /**
     * Retrieves position.
     *
     * @return Object of position.
     */
    public JSONObject getPosition() throws Exception {
        JSONObject response = null;
        String url = routes.get("api.order.rms.position");
        response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
        return response.isNull("data") ? null : response.getJSONObject("data");
    }

    /**
     * Retrieves conversion.
     *
     * @return Object of conversion.
     * @throws SmartAPIException is thrown for all Smart API trade related errors.
     * @throws JSONException     is thrown when there is exception while parsing
     *                           response.
     * @throws IOException       is thrown when there is connection error.
     */
    public JSONObject convertPosition(JSONObject params) throws Exception {
        JSONObject response = null;
        String url = routes.get("api.order.rms.position.convert");
        response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        return response;
    }

    /**
     * Create a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public Gtt gttCreateRule(Gtt gttParams) throws Exception {
        Gtt gtt = null;
        String url = routes.get("api.gtt.create");
        JSONObject params = new JSONObject();
        if (gttParams.getTradingSymbol() != null)
            params.put("tradingsymbol", gttParams.getTradingSymbol());
        if (gttParams.getSymbolToken() != null)
            params.put("symboltoken", gttParams.getSymbolToken());
        if (gttParams.getExchange() != null)
            params.put("exchange", gttParams.getExchange());
        if (gttParams.getTransactionType() != null)
            params.put("transactiontype", gttParams.getTransactionType());
        if (gttParams.getProductType() != null)
            params.put("producttype", gttParams.getProductType());
        if (gttParams.getPrice() != null)
            params.put("price", gttParams.getPrice());
        if (gttParams.getQuantity() != null)
            params.put("qty", gttParams.getQuantity());
        if (gttParams.getTriggerPrice() != null)
            params.put("triggerprice", gttParams.getTriggerPrice());
        if (gttParams.getDisclosedQty() != null)
            params.put("disclosedqty", gttParams.getDisclosedQty());
        if (gttParams.getTimePeriod() != null)
            params.put("timeperiod", gttParams.getTimePeriod());

        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(response) && Objects.nonNull(response.getJSONObject("data"))) {
            gtt = Utils.convertJsonObject(response.getJSONObject("data"), Gtt.class);
        }
        return gtt;
    }

    /**
     * Modify a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public Gtt gttModifyRule(Integer id, Gtt gttParams) throws Exception {
        Gtt gtt = null;
        String url = routes.get("api.gtt.modify");
        JSONObject params = new JSONObject();
        if (gttParams.getSymbolToken() != null)
            params.put("symboltoken", gttParams.getSymbolToken());
        if (gttParams.getExchange() != null)
            params.put("exchange", gttParams.getExchange());
        if (gttParams.getPrice() != null)
            params.put("price", gttParams.getPrice());
        if (gttParams.getQuantity() != null)
            params.put("qty", gttParams.getQuantity());
        if (gttParams.getTriggerPrice() != null)
            params.put("triggerprice", gttParams.getTriggerPrice());
        if (gttParams.getDisclosedQty() != null)
            params.put("disclosedqty", gttParams.getDisclosedQty());
        if (gttParams.getTimePeriod() != null)
            params.put("timeperiod", gttParams.getTimePeriod());
        params.put("id", id);

        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(response) && Objects.nonNull(response.getJSONObject("data"))) {
            gtt = Utils.convertJsonObject(response.getJSONObject("data"), Gtt.class);
        }
        return gtt;
    }

    /**
     * Cancel a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public Gtt gttCancelRule(Integer id, String symboltoken, String exchange) throws Exception {
        Gtt gtt = null;
        JSONObject params = new JSONObject();
        params.put("id", id);
        params.put("symboltoken", symboltoken);
        params.put("exchange", exchange);

        String url = routes.get("api.gtt.cancel");
        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(response) && Objects.nonNull(response.getJSONObject("data"))) {
            gtt = Utils.convertJsonObject(response.getJSONObject("data"), Gtt.class);
        }
        return gtt;
    }

    /**
     * Get Gtt Rule Details.
     *
     * @param id is gtt rule id.
     * @return returns the details of gtt rule.
     */

    public Gtt gttRuleDetails(Integer id) throws Exception {
        Gtt gtt = null;
        JSONObject params = new JSONObject();
        params.put("id", id);

        String url = routes.get("api.gtt.details");
        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        if (Objects.nonNull(response) && Objects.nonNull(response.getJSONObject("data"))) {
            gtt = Utils.convertJsonObject(response.getJSONObject("data"), Gtt.class);
        }
        return gtt;
    }

    /**
     * Get Gtt Rule Details.
     *
     * @param status is list of gtt rule status.
     * @param page   is no of page
     * @param count  is the count of gtt rules
     * @return returns the detailed list of gtt rules.
     */
    public List<Gtt> gttRuleList(List<String> status, Integer page, Integer count) {
        List<Gtt> gtts = null;
        try {
            JSONObject params = new JSONObject();
            params.put("status", status);
            params.put("page", page);
            params.put("count", count);

            String url = routes.get("api.gtt.list");
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            if (Objects.nonNull(response) && Objects.nonNull(response.getJSONObject("data"))) {
                gtts = Utils.convertJsonArrayToList(response.getJSONArray("data"), Gtt.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return gtts;
    }

    /**
     * Get Historic Data.
     *
     * @param params is historic data params.
     * @return returns the details of historic data.
     */
    public JSONArray candleData(JSONObject params) throws Exception {
        String url = routes.get("api.candle.data");
        JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        log.info("response : {}", response);
        return response.getJSONArray("data");
    }

    /**
     * Get Search Script Data.
     *
     * @param payload is Search Script params.
     * @return returns the details of Search Script data.
     */

    public JSONArray getSearchScrip(JSONObject payload) throws SmartAPIException, IOException {
        JSONArray scrip = null;
        try {
            String url = routes.get("api.search.script.data");
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, payload, accessToken);
            scrip = response.getJSONArray("data");
        } catch (IOException ex) {
            log.error("{} while generating session {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in generating Session  %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while generating session {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in generating Session %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (Exception ex) {
            log.error("{} while generating session {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in generating Session %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
        return scrip;
    }

    public String getInstrumentList() throws Exception {
        String instrumentList = null;
        String url = routes.getRaw("api.instrument.list");
        return smartAPIRequestHandler.getRequestJSONObject(this.apiKey, url, accessToken);
    }

    /**
     * Get Market Data.
     *
     * @param params is market data params.
     * @return returns the details of market data.
     */
    public JSONObject marketData(JSONObject params) throws Exception {
        try {
            String url = routes.get("api.market.data");
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            return response.isNull("data") ? null : response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    /**
     * Logs out user by invalidating the access token.
     *
     * @return JSONObject which contains status
     */

    public JSONObject logout() throws Exception {
        JSONObject response = null;
        String url = routes.get("api.user.logout");
        JSONObject params = new JSONObject();
        params.put("clientcode", this.userId);
        response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
        response.getJSONObject("data");
        return response;
    }

    /**
     * Get Margin Data.
     *
     * @param marginParams is margin data params.
     * @return returns the response of margin data.
     */
    public JSONObject getMarginDetails(List<MarginParams> marginParams) throws Exception {
        try {
            JSONArray positionsArray = new JSONArray();

            for (MarginParams params : marginParams) {
                JSONObject position = new JSONObject();
                position.put("exchange", params.exchange);
                position.put("qty", params.quantity);
                position.put("price", params.price);
                position.put("productType", params.productType);
                position.put("token", params.token);
                position.put("tradeType", params.tradeType);
                positionsArray.put(position);
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("positions", positionsArray);

            String url = routes.get("api.margin.batch");
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, requestBody, accessToken);
            return response.isNull("data") ? null : response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while fetching margin data {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s  while fetching margin data %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{}  while fetching margin data {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s  while fetching margin data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{}  while fetching margin data {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s  while fetching margin data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    /**
     * Get Individual Order Details
     *
     * @return JSONObject which contains order details from Smart API
     */
    public JSONObject getIndividualOrderDetails(String orderId) throws Exception {
        try {
            String url = routes.get("api.individual.order").concat(orderId);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return response.isNull("data") ? null : response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while getting individual order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in getting individual order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while getting individual order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s  while fetching margin data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{}  while getting individual order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s  while fetching margin data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }
}

