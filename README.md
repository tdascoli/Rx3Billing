# Rx3Billing
[![](https://jitpack.io/v/tdascoli/Rx3Billing.svg)](https://jitpack.io/#tdascoli/Rx3Billing)

[RxJava 3.0](https://github.com/ReactiveX/RxJava/tree/3.x) wrapper on Google Play Billing library.

This repository started as a personal usage of github user [hero](https://github.com/mu29) rx-billing library. You can check his work [here](https://github.com/mu29/rx-billing).

## Download

##### Gradle:

```groovy
dependencies {
  implementation 'com.github.tdascoli:Rx3Billing:1.0.0'
}
```
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

## Usage

### Initialize RxBilling Instance

```java

RxBilling billingClient;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    billingClient = new RxBilling().set(this);
}
```

...or you can inject with dagger

```java
@Provides
@PerApplication
public RxBilling provideRxBilling() {
    return new RxBilling()
}
```

```java
@Inject
RxBilling billingClient;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    billingClient.set(this);
}
```

### Purchase

```java
billingClient
    .purchase("product.test.1", null, BillingClient.SkuType.INAPP)
    .subscribe(purchases -> Log.d("IAP", purchases.get(0).getOrderId()));
```

### Query purchases

```java
billingClient
    .queryPurchases()
    .subscribe(purchases -> {
        for (Purchase purchase : purchases) {
            Log.d("IAP", purchase.getOrderId());
        }
    });
```

### Get sku details

```java
List<String> ids = new ArrayList<>();
ids.add("test.product.1");

billingClient
    .getSkuDetails(ids)
    .subscribe(details -> {
        for (SkuDetails detail: details) {
            Log.d("IAP", detail.getPrice());
        }
    });
```

### Consume

```java
billingClient
    .consume("purchaseToken")
    .subscribe(token -> Log.d("IAP", token));
```

<a href='https://ko-fi.com/H2H32EWM1' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://cdn.ko-fi.com/cdn/kofi1.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

## License

	MIT License

	Copyright (c) 2020 Thomas D'Ascoli

	Permission is hereby granted, free of charge, to any person obtaining a 
	copy of this software and associated documentation files (the "Software"), 
	to deal in the Software without restriction, including without limitation 
	the rights to use, copy, modify, merge, publish, distribute, sublicense, 
	and/or sell copies of the Software, and to permit persons to whom the 
	Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included 
	in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
	OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
	THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
	OTHER DEALINGS IN THE SOFTWARE.
