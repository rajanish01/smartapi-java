package com.angelbroking.smartapi.models;

import lombok.Data;

/**
 * A wrapper for order params to be sent while placing an order.
 */
@Data
public class OrderParams {

    private String orderid;
    /**
     * Exchange in which instrument is listed (NSE, BSE, NFO, BFO, CDS, MCX).
     */

    private String exchange;

    /**
     * symboltoken of the instrument.
     */

    private String symboltoken;

    /**
     * Transaction type (BUY or SELL).
     */

    private String transactiontype;

    /**
     * Order quantity
     */

    private Integer quantity;

    /**
     * Order Price
     */

    private Double price;

    /**
     * producttype code (NRML, MIS, CNC).
     */

    private String producttype;

    /**
     * Order type (LIMIT, SL, SL-M, MARKET).
     */

    private String ordertype;

    /**
     * Order duration (DAY, IOC).
     */

    private String duration;

    /**
     * variety
     */

    private String variety;

    /**
     * Order duration (DAY, IOC).
     */

    private String tradingsymbol;

    private String triggerprice;

    private String squareoff;

    private String stoploss;

}