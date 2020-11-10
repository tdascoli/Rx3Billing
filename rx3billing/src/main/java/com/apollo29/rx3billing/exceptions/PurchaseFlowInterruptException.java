package com.apollo29.rx3billing.exceptions;

public class PurchaseFlowInterruptException extends Throwable {
    @Override
    public String getMessage() {
        return "Other purchase flow is initiated.";
    }
}
