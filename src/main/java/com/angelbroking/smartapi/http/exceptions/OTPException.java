package com.angelbroking.smartapi.http.exceptions;

public class OTPException extends SmartAPIException{

    private static final long serialVersionUID = 1L;

    public OTPException(String message, String code){
        super(message, code);
    }

}
