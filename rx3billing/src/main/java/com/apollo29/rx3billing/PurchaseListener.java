package com.apollo29.rx3billing;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.apollo29.rx3billing.exceptions.PurchaseFailureException;

import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

class PurchaseListener implements PurchasesUpdatedListener {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private SingleEmitter<List<Purchase>> emitter = null;

    void setPurchaseEmitter(SingleEmitter<List<Purchase>> newEmitter) {
        if (emitter != null && !emitter.isDisposed()) {
            disposable.dispose();
        }

        emitter = newEmitter;
        emitter.setDisposable(disposable);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (emitter == null || emitter.isDisposed()) {
            return;
        }

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            emitter.onSuccess(purchases);
        } else {
            emitter.onError(new PurchaseFailureException(billingResult.getResponseCode()));
        }
    }

    public void dispose(){
        disposable.clear();
    }
}
