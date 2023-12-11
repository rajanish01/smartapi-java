package com.angelbroking.smartapi.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A wrapper for order.
 */
@Data
public class Order {

    @JsonProperty("disclosedquantity")
    private String disclosedQuantity;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("variety")
    private String variety;

    @JsonProperty("ordertype")
    private String orderType;

    @JsonProperty("triggerprice")
    private String triggerPrice;

    @JsonProperty("text")
    private String text;

    @JsonProperty("price")
    private String price;

    @JsonProperty("status")
    private String status;

    @JsonProperty("producttype")
    private String productType;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("orderid")
    private String orderId;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("updatetime")
    private String updateTime;

    @JsonProperty("exchtime")
    private String exchangeTimestamp;

    @JsonProperty("exchorderupdatetime")
    private String exchangeUpdateTimestamp;

    @JsonProperty("averageprice")
    private String averagePrice;

    @JsonProperty("transactiontype")
    private String transactionType;

    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("squareoff")
    private String squareOff;

    @JsonProperty("stoploss")
    private String stopLoss;

    @JsonProperty("trailingstoploss")
    private String trailingStopLoss;

    @JsonProperty("symboltoken")
    private String symbolToken;

    @JsonProperty("instrumenttype")
    private String instrumentType;

    @JsonProperty("strikeprice")
    private String strikePrice;

    @JsonProperty("optiontype")
    private String optionType;

    @JsonProperty("expirydate")
    private String expiryDate;

    @JsonProperty("lotsize")
    private String lotSize;

    @JsonProperty("cancelsize")
    private String cancelSize;

    @JsonProperty("filledshares")
    private String filledShares;

    @JsonProperty("orderstatus")
    private String orderStatus;

    @JsonProperty("unfilledshares")
    private String unfilledShares;

    @JsonProperty("fillid")
    private String fillId;

    @JsonProperty("filltime")
    private String fillTime;
}
