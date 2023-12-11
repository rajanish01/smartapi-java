package com.angelbroking.smartapi.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A wrapper for order.
 */
@Data
public class Order {

    @JsonProperty("variety")
    private String variety;

    @JsonProperty("ordertype")
    private String orderType;

    @JsonProperty("producttype")
    private String productType;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("price")
    private String price;

    @JsonProperty("triggerprice")
    private String triggerPrice;

    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("disclosedquantity")
    private String disclosedQuantity;

    @JsonProperty("squareoff")
    private String squareOff;

    @JsonProperty("stoploss")
    private String stopLoss;

    @JsonProperty("trailingstoploss")
    private String trailingStopLoss;

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("transactiontype")
    private String transactionType;

    @JsonProperty("exchange")
    private String exchange;

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

    @JsonProperty("averageprice")
    private String averagePrice;

    @JsonProperty("filledshares")
    private String filledShares;

    @JsonProperty("unfilledshares")
    private String unfilledShares;

    @JsonProperty("orderid")
    private String orderId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("status")
    private String status;

    @JsonProperty("orderstatus")
    private String orderStatus;

    @JsonProperty("updatetime")
    private String updateTime;

    @JsonProperty("exchtime")
    private String exchTime;

    @JsonProperty("exchorderupdatetime")
    private String exchOrderUpdateTime;

    @JsonProperty("fillid")
    private String fillId;

    @JsonProperty("filltime")
    private String fillTime;

    @JsonProperty("parentorderid")
    private String parentOrderId;

    @JsonProperty("ordertag")
    private String orderTag;

    @JsonProperty("uniqueorderid")
    private String uniqueOrderId;
}
