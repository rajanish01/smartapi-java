package com.angelbroking.smartapi.http.exceptions;

/**
 * This is the base exception class which has a publicly accessible message and
 * code that is received from Angel Connect api.
 */

public class SmartAPIException extends Exception {

    private static final long serialVersionUID = 1L;
    public String code;
    public String message;

    public SmartAPIException(String message, String code) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SmartAPIException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SmartAPIException [message=" + message + ", code=" + code + "]";
    }

}
