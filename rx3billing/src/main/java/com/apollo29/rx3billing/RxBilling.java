package com.apollo29.rx3billing;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.apollo29.rx3billing.exceptions.ConsumeFailureException;
import com.apollo29.rx3billing.exceptions.QueryPurchaseFailureException;
import com.apollo29.rx3billing.exceptions.SkuDetailsFailureException;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class RxBilling {

    private Activity activity;
    private BillingClient client;
    private final PurchaseListener purchaseListener = new PurchaseListener();

    public RxBilling set(Activity activity) {
        this.activity = activity;
        client = BillingClient.newBuilder(activity).setListener(purchaseListener).build();
        return this;
    }

    public Single<List<Purchase>> purchase(SkuDetails skuDetails) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    purchaseListener.setPurchaseEmitter(emitter);
                    BillingFlowParams params = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        //.setType(billingType)
                        //.setOldSkus(oldSkus)
                        .build();
                    client.launchBillingFlow(activity, params);
                },
                emitter::onError
            )
        );
    }

    public Single<List<Purchase>> queryPurchases() {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    Purchase.PurchasesResult result = client.queryPurchases(BillingClient.SkuType.INAPP);

                    if (areSubscriptionsSupported()) {
                        Purchase.PurchasesResult subscriptionResult = client.queryPurchases(BillingClient.SkuType.SUBS);
                        if (subscriptionResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            result.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                        }
                    }

                    int code = result.getResponseCode();
                    if (code == BillingClient.BillingResponseCode.OK) {
                        emitter.onSuccess(result.getPurchasesList());
                    } else {
                        emitter.onError(new QueryPurchaseFailureException(code));
                    }
                },
                emitter::onError
            )
        );
    }

    public Single<List<SkuDetails>> getSkuDetails(List<String> skuList) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.INAPP)
                        .build();

                    client.querySkuDetailsAsync(params, (result, purchases) -> {
                        if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            emitter.onSuccess(purchases);
                        } else {
                            emitter.onError(new SkuDetailsFailureException(result.getResponseCode()));
                        }
                    });
                },
                emitter::onError
            )
        );
    }

    public Single<String> consume(ConsumeParams params) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> client.consumeAsync(params, (result, purchaseToken) -> {
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        emitter.onSuccess(purchaseToken);
                    } else {
                        emitter.onError(new ConsumeFailureException(result.getResponseCode()));
                    }
                }),
                emitter::onError
            )
        );
    }

    private Completable tryConnect() {
        return Completable.create(emitter -> {
            if (client.isReady()) {
                emitter.onComplete();
                return;
            }

            client.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    emitter.onComplete();
                }

                @Override
                public void onBillingServiceDisconnected() {}
            });
        });
    }

    private boolean areSubscriptionsSupported() {
        BillingResult result = client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        return result.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }
}
