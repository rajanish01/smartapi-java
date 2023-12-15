package com.angelbroking.smartapi.http.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the base exception class which has a publicly accessible message and
 * code that is received from Angel Connect api.
 */

@Slf4j
public class SmartAPIException extends Exception {

    private static final long serialVersionUID = 1L;
    public String code;
    public String message;

    public SmartAPIException(String message, String code) {
        super(message);
        this.code = code;
        this.message = message;
        log.error(message);
    }

    public SmartAPIException(String message) {
        super(message);
        this.message = message;
        log.error(message);
    }

    @Override
    public String toString() {
        return "SmartAPIException [message=" + message + ", code=" + code + "]";
    }

}
