package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.*;
import com.angelbroking.smartapi.models.SearchScripResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.angelbroking.smartapi.utils.Constants.APIKEY_EXCEPTION_MESSAGE;
import static com.angelbroking.smartapi.utils.Constants.TOKEN_EXCEPTION_MESSAGE;

/**
 * Response handler for handling all the responses.
 */
@Slf4j
public class SmartAPIResponseHandler {

    private static final String EMPTY_STRING = "";

    public JSONObject handle(Response response, String body) throws IOException, SmartAPIException, JSONException {
        if (Objects.requireNonNull(response.header("Content-Type")).contains("json")) {
            JSONObject jsonObject = new JSONObject(body);
            if (!jsonObject.getString("errorcode").equals(EMPTY_STRING)) {
                throw dealWithException(jsonObject, jsonObject.getString("errorcode"));
            } else if (!jsonObject.getString("errorcode").equals(EMPTY_STRING)) {
                throw dealWithException(jsonObject, jsonObject.getString("errorCode"));
            }
            return jsonObject;
        } else {
            assert response.body() != null;
            throw new DataException("Unexpected content type received from server: " + response.header("Content-Type")
                    + " " + response.body().string(), "AG8001");
        }
    }

    private SmartAPIException dealWithException(JSONObject jsonObject, String code) throws JSONException {

        switch (code) {
            // if there is a token exception, generate a signal to logout the user.
            case "AG8003":
            case "AB8050":
            case "AB8051":
            case "AB1010":
                if (SmartConnect.sessionExpiryHook != null) {
                    SmartConnect.sessionExpiryHook.sessionExpired();
                }
                return new TokenException(jsonObject.getString("message"), code);

            case "AG8001":
                return new TokenException(TOKEN_EXCEPTION_MESSAGE, code);
            case "AG8002":
                return new DataException(jsonObject.getString("message"), code);

            case "AB1004":
            case "AB2000":
                return new GeneralException(jsonObject.getString("message"), code);

            case "AB1003":
            case "AB1005":
            case "AB1012":
            case "AB1002":
                return new InputException(jsonObject.getString("message"), code);

            case "AB1008":
            case "AB1009":
            case "AB1013":
            case "AB1014":
            case "AB1015":
            case "AB1016":
            case "AB1017":
                return new OrderException(jsonObject.getString("message"), code);

            case "NetworkException":
                return new NetworkException(jsonObject.getString("message"), code);

            case "AB1000":
            case "AB1001":
            case "AB1011":
                return new PermissionException(jsonObject.getString("message"), code);
            case "AG8004":
                return new ApiKeyException(APIKEY_EXCEPTION_MESSAGE, code);
            case "AB1050":
                return new OTPException(jsonObject.getString("message"), code);
            default:
                return new SmartAPIException(jsonObject.getString("data not found"));
        }
    }

    public String handler(Response response, String body) throws SmartAPIException, JSONException, IOException {
        if (response.code() == 200) {
            return handleResponse(response, body);
        } else if (response.code() == 400) {
            log.error("Bad request. Please provide valid input");
            return "Bad request. Please provide valid input";
        } else {
            log.error("Response or response body is null.");
            throw new IllegalArgumentException("Response or response body is null.");
        }
    }

    private String handleResponse(Response response, String body) throws SmartAPIException, IOException {
        try {
            JSONObject responseBodyJson = new JSONObject(body);
            if (responseBodyJson.getBoolean("status")) {
                JSONArray dataArray = responseBodyJson.optJSONArray("data");
                if (dataArray != null && dataArray.length() > 0) {
                    List<SearchScripResponseDTO> stockDTOList = parseStockDTOList(dataArray);

                    StringBuilder result = new StringBuilder();
                    result.append("Search successful. Found ").append(stockDTOList.size()).append(" trading symbols for the given query:\n");

                    int index = 1;
                    for (SearchScripResponseDTO stockDTO : stockDTOList) {
                        result.append(index).append(". exchange: ").append(stockDTO.getExchange()).append(", tradingsymbol: ").append(stockDTO.getTradingSymbol()).append(", symboltoken: ").append(stockDTO.getSymbolToken()).append("\n");
                        index++;
                    }
                    return result.toString();
                } else {
                    return "Search successful. No matching trading symbols found for the given query.";
                }
            } else {
                return String.valueOf(handle(response, body));
            }

        } catch (JSONException e) {
            log.error("Error parsing response body as JSON.", e.getMessage());
            throw new SmartAPIException("Error parsing response body as JSON.");
        }
    }

    private List<SearchScripResponseDTO> parseStockDTOList(JSONArray dataArray) throws JSONException, SmartAPIException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(dataArray.toString(), new TypeReference<List<SearchScripResponseDTO>>() {
            });
        } catch (IOException e) {
            log.error("Error parsing JSON data array.", e);
            throw new SmartAPIException("Error parsing JSON data array.");
        }
    }
}
