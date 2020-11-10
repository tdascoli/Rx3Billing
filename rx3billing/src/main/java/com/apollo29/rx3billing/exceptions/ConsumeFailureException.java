package com.apollo29.rx3billing.exceptions;

public class ConsumeFailureException extends Throwable {

    private int code;

    public ConsumeFailureException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "Purchase failed with response code " + code;
    }
}
