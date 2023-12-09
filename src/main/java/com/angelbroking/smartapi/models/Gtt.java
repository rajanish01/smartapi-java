package com.angelbroking.smartapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Gtt {
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("tradingsymbol")
	private String tradingSymbol;
	@JsonProperty("symboltoken")
	private String symbolToken;
	@JsonProperty("exchange")
	private String exchange;
	@JsonProperty("transactiontype")
	private String transactionType;
	@JsonProperty("producttype")
	private String productType;
	@JsonProperty("price")
	private BigDecimal price;
	@JsonProperty("quantity")
	private Integer quantity;
	@JsonProperty("triggerprice")
	private BigDecimal triggerPrice;
	@JsonProperty("disclosedqty")
	private Integer disclosedQty;
	@JsonProperty("timeperiod")
	private Integer timePeriod;
	@JsonProperty("status")
	private String status;
	@JsonProperty("createddate")
	private String createdDate;
	@JsonProperty("updateddate")
	private String updatedDate;
	@JsonProperty("expirydate")
	private String expiryDate;
	@JsonProperty("clientid")
	private String clientId;
}
