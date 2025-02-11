package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.orderupdate.OrderUpdateListener;
import com.angelbroking.smartapi.orderupdate.SmartStreamOrderUpdate;
import com.angelbroking.smartapi.smartTicker.*;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.ticker.OnConnect;
import com.angelbroking.smartapi.ticker.OnTicks;
import com.angelbroking.smartapi.ticker.SmartAPITicker;
import com.angelbroking.smartapi.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j

public class Examples {

    public void getProfile(SmartConnect smartConnect) throws Exception {
        User profile = smartConnect.getProfile();
    }

    /** CONSTANT Details */

    /* VARIETY */
    /*
     * VARIETY_NORMAL: Normal Order (Regular)
     * VARIETY_AMO: After Market Order
     * VARIETY_STOPLOSS: Stop loss order
     * VARIETY_ROBO: ROBO (Bracket) Order
     */
    /* TRANSACTION TYPE */
    /*
     * TRANSACTION_TYPE_BUY: Buy TRANSACTION_TYPE_SELL: Sell
     */

    /* ORDER TYPE */
    /*
     * ORDER_TYPE_MARKET: Market Order(MKT)
     * ORDER_TYPE_LIMIT: Limit Order(L)
     * ORDER_TYPE_STOPLOSS_LIMIT: Stop Loss Limit Order(SL)
     * ORDER_TYPE_STOPLOSS_MARKET: Stop Loss Market Order(SL-M)
     */

    /* PRODUCT TYPE */
    /*
     * PRODUCT_DELIVERY: Cash & Carry for equity (CNC)
     * PRODUCT_CARRYFORWARD: Normal
     * for futures and options (NRML)
     * PRODUCT_MARGIN: Margin Delivery
     * PRODUCT_INTRADAY: Margin Intraday Squareoff (MIS)
     * PRODUCT_BO: Bracket Order
     * (Only for ROBO)
     */

    /* DURATION */
    /*
     * DURATION_DAY: Valid for a day
     * DURATION_IOC: Immediate or Cancel
     */

    /* EXCHANGE */
    /*
     * EXCHANGE_BSE: BSE Equity
     * EXCHANGE_NSE: NSE Equity
     * EXCHANGE_NFO: NSE Future and Options
     * EXCHANGE_CDS: NSE Currency
     * EXCHANGE_NCDEX: NCDEX Commodity
     * EXCHANGE_MCX: MCX Commodity
     */

    /**
     * Place order.
     */
    public void placeOrder(SmartConnect smartConnect) throws Exception {

        OrderParams orderParams = new OrderParams();
        orderParams.setVariety(Constants.VARIETY_STOPLOSS);
        orderParams.setQuantity(1);
        orderParams.setSymboltoken("1660");
        orderParams.setExchange(Constants.EXCHANGE_NSE);
        orderParams.setOrdertype(Constants.ORDER_TYPE_STOPLOSS_LIMIT);
        orderParams.setTradingsymbol("ITC-EQ");
        orderParams.setProducttype(Constants.PRODUCT_INTRADAY);
        orderParams.setDuration(Constants.DURATION_DAY);
        orderParams.setTransactiontype(Constants.TRANSACTION_TYPE_BUY);
        orderParams.setPrice(122.2);
        orderParams.setTriggerprice("209");

        Order order = smartConnect.placeOrder(orderParams, "STOPLOSS");
        log.info("order : {}", order);
    }

    /**
     * Modify order.
     */
    public void modifyOrder(SmartConnect smartConnect) throws Exception {
        // Order modify request will return order model which will contain only
        OrderParams orderParams = new OrderParams();
        orderParams.setQuantity(1);
        orderParams.setOrdertype(Constants.ORDER_TYPE_LIMIT);
        orderParams.setTradingsymbol("ASHOKLEY");
        orderParams.setSymboltoken("3045");
        orderParams.setProducttype(Constants.PRODUCT_DELIVERY);
        orderParams.setExchange(Constants.EXCHANGE_NSE);
        orderParams.setDuration(Constants.DURATION_DAY);
        orderParams.setPrice(122.2);

        String orderId = "201216000755110";
        Order order = smartConnect.modifyOrder(orderId, orderParams, Constants.VARIETY_NORMAL);
    }

    /**
     * Cancel an order
     */
    public void cancelOrder(SmartConnect smartConnect) throws Exception {
        // Order modify request will return order model which will contain only
        // order_id.
        // Cancel order will return order model which will only have orderId.
        Order order = smartConnect.cancelOrder("201009000000015", Constants.VARIETY_NORMAL);
    }

    /**
     * Get order details
     */
    public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
        JSONObject orders = smartConnect.getOrderHistory(smartConnect.getUserId());
        log.info("orders {} ", orders);
    }

    /**
     * Get last price for multiple instruments at once. USers can either pass
     * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
     * 50, BSE:SENSEX} or {256265, 265}
     */
    public void getLTP(SmartConnect smartConnect) throws Exception {
        String exchange = "NSE";
        String tradingSymbol = "SBIN-EQ";
        String symboltoken = "3045";
        JSONObject ltpData = smartConnect.getLTP(exchange, tradingSymbol, symboltoken);
    }

    /**
     * Get tradebook
     */
    public void getTradeBook(SmartConnect smartConnect) throws SmartAPIException, IOException {
        // Returns tradebook.
        List<Order> trades = smartConnect.getTradeBook();

    }

    /**
     * Get RMS
     */
    public void getRMS(SmartConnect smartConnect) throws Exception {
        // Returns RMS.
        JSONObject response = smartConnect.getRMS();
    }

    /**
     * Get Holdings
     */
    public void getHolding(SmartConnect smartConnect) throws Exception {
        // Returns Holding.
        JSONArray response = smartConnect.getHolding();
    }

    /**
     * Get All Holdings
     */
    public void getAllHolding(SmartConnect smartConnect) throws Exception {
        // Returns All Holding.
        JSONObject response = smartConnect.getAllHolding();
        log.info("response : ", response);
    }

    /**
     * Get Position
     */
    public void getPosition(SmartConnect smartConnect) throws Exception {
        // Returns Position.
        JSONObject response = smartConnect.getPosition();
    }

    /**
     * convert Position
     */
    public void convertPosition(SmartConnect smartConnect) throws Exception {

        JSONObject requestObejct = new JSONObject();
        requestObejct.put("exchange", "NSE");
        requestObejct.put("oldproducttype", "DELIVERY");
        requestObejct.put("newproducttype", "MARGIN");
        requestObejct.put("tradingsymbol", "SBIN-EQ");
        requestObejct.put("transactiontype", "BUY");
        requestObejct.put("quantity", 1);
        requestObejct.put("type", "DAY");

        JSONObject response = smartConnect.convertPosition(requestObejct);
    }

    /**
     * Create Gtt Rule
     */
    public void createRule(SmartConnect smartConnect) throws Exception {
        Gtt gttParams = Gtt.builder()
                .tradingSymbol("SBIN-EQ")
                .symbolToken("3045")
                .exchange("NSE")
                .productType("MARGIN")
                .transactionType("BUY")
                .price(BigDecimal.valueOf(100000.01))
                .disclosedQty(10)
                .triggerPrice(BigDecimal.valueOf(20000.1))
                .timePeriod(300)
                .build();
        Gtt gtt = smartConnect.gttCreateRule(gttParams);
    }

    /**
     * Modify Gtt Rule
     */
    public void modifyRule(SmartConnect smartConnect) throws Exception {
        Gtt gttParams = Gtt.builder()
                .tradingSymbol("SBIN-EQ")
                .symbolToken("3045")
                .exchange("NSE")
                .productType("MARGIN")
                .transactionType("BUY")
                .price(BigDecimal.valueOf(100000.01))
                .disclosedQty(10)
                .triggerPrice(BigDecimal.valueOf(20000.1))
                .timePeriod(300)
                .build();
        Integer id = 1000051;
        Gtt gtt = smartConnect.gttModifyRule(id, gttParams);
    }

    /**
     * Cancel Gtt Rule
     */
    public void cancelRule(SmartConnect smartConnect) throws Exception {
        Integer id = 1000051;
        String symboltoken = "3045";
        String exchange = "NSE";

        Gtt gtt = smartConnect.gttCancelRule(id, symboltoken, exchange);
    }

    /**
     * Gtt Rule Details
     */
    public void ruleDetails(SmartConnect smartConnect) throws Exception {
        Integer id = 1000051;

        Gtt gtt = smartConnect.gttRuleDetails(id);
    }

    /**
     * Gtt Rule Lists
     */
    @SuppressWarnings("serial")
    public void ruleList(SmartConnect smartConnect) throws SmartAPIException, IOException {

        List<String> status = new ArrayList<String>() {
            {
                add("NEW");
                add("CANCELLED");
                add("ACTIVE");
                add("SENTTOEXCHANGE");
                add("FORALL");
            }
        };
        Integer page = 1;
        Integer count = 10;

        List<Gtt> gtt = smartConnect.gttRuleList(status, page, count);
    }

    /**
     * Historic Data
     */
    public void getCandleData(SmartConnect smartConnect) throws Exception {

        JSONObject requestObejct = new JSONObject();
        requestObejct.put("exchange", "NSE");
        requestObejct.put("symboltoken", "3045");
        requestObejct.put("interval", "ONE_MINUTE");
        requestObejct.put("fromdate", "2021-03-08 09:00");
        requestObejct.put("todate", "2021-03-09 09:20");

        JSONArray response = smartConnect.candleData(requestObejct);
    }


    /**
     * Search Scrip Data
     */
    public void getSearchScrip(SmartConnect smartConnect) throws SmartAPIException, IOException {
        JSONObject payload = new JSONObject();
        payload.put("exchange", "MCX");
        payload.put("searchscrip", "Crude");
        smartConnect.getSearchScrip(payload);
    }

    /**
     * Market Data
     * To Retrieve Market Data with different modes use.
     * e.g:
     * payload.put("mode", "FULL");
     * payload.put("mode", "LTP");
     * payload.put("mode", "OHLC");
     */
    public void getMarketData(SmartConnect smartConnect) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("mode", "FULL"); // You can change the mode as needed
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        JSONObject response = smartConnect.marketData(payload);
    }


    public void tickerUsage(String clientId, String feedToken, String strWatchListScript, String task)
            throws SmartAPIException {

        SmartAPITicker tickerProvider = new SmartAPITicker(clientId, feedToken, strWatchListScript, task);

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                log.info("subscribe() called!");
                tickerProvider.subscribe();
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(JSONArray ticks) {
                log.info("ticker data: " + ticks.toString());
            }
        });

        /**
         * connects to Smart API ticker server for getting live quotes
         */
        tickerProvider.connect();

        /**
         * You can check, if websocket connection is open or not using the following
         * method.
         */
        boolean isConnected = tickerProvider.isConnectionOpen();
        log.info("is connected {} ", isConnected);

        // After using SmartAPI ticker, close websocket connection.
        // tickerProvider.disconnect();

    }

    public void smartWebSocketUsage(String clientId, String jwtToken, String apiKey, String actionType, String feedType)
            throws SmartAPIException {

        SmartWebsocket smartWebsocket = new SmartWebsocket(clientId, jwtToken, apiKey, actionType, feedType);

        smartWebsocket.setOnConnectedListener(new SmartWSOnConnect() {

            @Override
            public void onConnected() {

                smartWebsocket.runscript();
            }
        });

        smartWebsocket.setOnDisconnectedListener(new SmartWSOnDisconnect() {
            @Override
            public void onDisconnected() {
                log.info("onDisconnected");
            }
        });

        /** Set error listener to listen to errors. */
        smartWebsocket.setOnErrorListener(new SmartWSOnError() {
            @Override
            public void onError(Exception exception) {
                log.info("onError: " + exception.getMessage());
            }

            @Override
            public void onError(SmartAPIException smartAPIException) {
                log.info("onError: " + smartAPIException.getMessage());
            }

            @Override
            public void onError(String error) {
                log.info("onError: " + error);
            }
        });

        smartWebsocket.setOnTickerArrivalListener(new SmartWSOnTicks() {
            @Override
            public void onTicks(JSONArray ticks) {
                log.info("ticker data: " + ticks.toString());
            }
        });

        /**
         * connects to Smart API ticker server for getting live quotes
         */
        smartWebsocket.connect();

        /**
         * You can check, if websocket connection is open or not using the following
         * method.
         */
        boolean isConnected = smartWebsocket.isConnectionOpen();
        log.info("is connected {}", isConnected);

        // After using SmartAPI ticker, close websocket connection.
        // smartWebsocket.disconnect();

    }

    /**
     * Logout user.
     */
    public void logout(SmartConnect smartConnect) throws Exception {
        /** Logout user and kill session. */
        JSONObject jsonObject = smartConnect.logout();
    }


    /**
     * Margin data.
     */
    public void getMarginDetails(SmartConnect smartConnect) throws Exception {
        List<MarginParams> marginParamsList = new ArrayList<>();
        MarginParams marginParams = new MarginParams();
        marginParams.quantity = 1;
        marginParams.token = "12740";
        marginParams.exchange = Constants.EXCHANGE_NSE;
        marginParams.productType = Constants.PRODUCT_DELIVERY;
        marginParams.price = 0.0;
        marginParams.tradeType = Constants.TRADETYPE_BUY;

        marginParamsList.add(marginParams);
        JSONObject jsonObject = smartConnect.getMarginDetails(marginParamsList);
        log.info("response {} ", jsonObject);

    }

    /**
     * Get Individual Order
     */
    public void getIndividualOrder(SmartConnect smartConnect, String orderId) throws Exception {
        JSONObject jsonObject = smartConnect.getIndividualOrderDetails(orderId);
        log.info("response {} ", jsonObject);
    }

    /**
     * Order update websocket
     * <p>
     * To retrieve order update websocket data
     *
     * @param accessToken
     */
    public void orderUpdateUsage(String accessToken) {
        SmartStreamOrderUpdate smartStreamOrderUpdate = new SmartStreamOrderUpdate(accessToken, new OrderUpdateListener() {
            /**
             * Check if the websocket is connected or not
             */
            @Override
            public void onConnected() {
                log.info("order update websocket connected");
            }

            /**
             * Handle the onDisconnected event
             */
            @Override
            public void onDisconnected() {

            }

            /**
             * Handle the onError event
             * @param error
             */
            @Override
            public void onError(SmartStreamError error) {

            }

            /**
             * Handle the onPong event
             */
            @Override
            public void onPong() {

            }

            /**
             * Handle the onOrderUpdate event
             * @param data
             */
            @Override
            public void onOrderUpdate(String data) {
                log.info("order update data {} ", data);
            }
        });
    }
}
