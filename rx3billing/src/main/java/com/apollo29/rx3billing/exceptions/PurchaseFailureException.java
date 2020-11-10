package com.apollo29.rx3billing.exceptions;

public class PurchaseFailureException extends Throwable {

    private int code;

    public PurchaseFailureException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "Purchase failed with response code " + code;
    }
}
