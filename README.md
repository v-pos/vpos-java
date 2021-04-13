# vPOS Java

![Build status](https://github.com/nextbss/vpos-java/workflows/Deploy%20to%20Main%20Branch/badge.svg)
[![](https://img.shields.io/badge/vPOS-OpenSource-blue.svg)](https://www.vpos.ao)

This java library helps you easily interact with the vPOS API,
Allowing merchants apps/services to request a payment from a client through his/her mobile phone number.

The service currently works for solutions listed below:

EMIS GPO (Multicaixa Express)

Want to know more about how we are empowering merchants in Angola? See our [website](https://vpos.ao)

### Features
- Simple interface
- Uniform plain old objects are returned by all official libraries, so you don't have
to serialize/deserialize the JSON returned by our API.

### Documentation
Does interacting directly with our API service sound better to you? 
See our documentation on [developer.vpos.ao](https://developer.vpos.ao)

## Installation
Add the package dependencies to the `dependencies` element of your project pom.xml file
```xml
<dependencies>
  <dependency>
    <groupId>ao.vpos.vpos</groupId>
    <artifactId>vpos</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## Use
### Create Instance

```java
// use the default environment variables option
var merchant = new Vpos();

# or use optional arguments option
var merchant = new Vpos('your_token_here');
```

Create an instance of `Vpos` (make sure to define the environment variables above to) to interact with our API.
The constructor will be responsible for acquiring the tokens defined above to interact with the API.

*Note:* All `Vpos()` methods return a common response object called `Response`.

This type is a wrapper for successful server response.

It has the following methods that allow you to acquire the necessary response data.

| Method | Description | Type |
| --- | --- | --- 
| `getStatusCode()` | Get status code from `Response` | `int` | 
| `getData` | Get data from response, if available | `T` | 
| `getMessage()` | Get the response message | `String` | 
| `getLocation()` | Get the Location of a Transaction, if applicable | `String` | 

### Configuration
This java library requires you set up the following environment variables on your machine before
interacting with the API using this library:

| Variable | Description | Required |
| --- | --- | --- |
| `GPO_POS_ID` | The Point of Sale ID provided by EMIS | true |
| `GPO_SUPERVISOR_CARD` | The Supervisor card ID provided by EMIS | true |
| `MERCHANT_VPOS_TOKEN` | The API token provided by vPOS | true |
| `PAYMENT_CALLBACK_URL` | The URL that will handle payment notifications | false |
| `REFUND_CALLBACK_URL` | The URL that will handle refund notifications | false |
| `VPOS_ENVIRONMENT` | The vPOS environment, leave empty for `sandbox` mode and use `"prd"` for `production`.  | false |

or using one of the optional arguments

#### Optional Arguments
| Argument | Description | Type |
| --- | --- | --- |
| `token` | Token generated at [vPOS](https://merchant.vpos.ao) dashboard | `string`
| `pos_id` | Merchant POS ID provided by EMIS | `string`
| `supervisor_card` | Merchant Supervisor Card number provided by EMIS | `string`
| `payment_callback_url` | Merchant application JSON endpoint to accept the callback payment response | `string`
| `refund_callback_url` | Merchant application JSON endpoint to accept the callback refund response | `string`
| `environment` | The vPOS environment, leave empty for `sandbox` mode and use `"PRD"` for `production`.  | `string` |

Don't have this information? [Talk to us](suporte@vpos.ao)

In the case of an unsuccessful response eg. authorization errors (401), bad request(401) or any other error, an
`ApiException` will be thrown so that your system can handle the error accordingly. 

```java
try {
     var response = merchant.newPayment("900111222", "199.99");
} catch (ApiException e) {
    
}
```

Given you have set up all the environment variables above with the correct information, you will now
be able to authenticate and communicate effectively with our API using this library. 

The next section will show the various payment actions that can be performed by you, the merchant.

### Get all Transactions
This endpoint retrieves all transactions.

```java
var transactions = merchant.getTransactions();
```

### Get a specific Transaction
Retrieves a transaction given a valid transaction ID.


```java
var transaction = merchant.getTransaction("1jHXEbRTIbbwaoJ6w86");
```

| Argument | Description | Type |
| --- | --- | --- |
| `id` | An existing Transaction ID | `string`

### New Payment Transaction
Creates a new payment transaction given a valid mobile number associated with a `MULTICAIXA` account
and a valid amount.

```java
var transaction = merchant.newPayment("900111222", "123.45");
```

| Argument | Description | Type |
| --- | --- | --- |
| `mobile` | The mobile number of the client who will pay | `string`
| `amount` | The amount the client should pay, eg. "259.99", "259000.00" | `string`

### Request Refund
Given an existing `parent_transaction_id`, request a refund.

```java
var transaction = merchant.newRefund("1jHXEbRTIbbwaoJ6w86");
```

| Argument | Description | Type |
| --- | --- | --- |
| `parent_transaction_id` | The ID of transaction you wish to refund | `string`

### Poll Transaction Status
Poll the status of a transaction given a valid `request_id`. 

Note: The `request_id` in this context is essentially the `transaction_id` of an existing request. 

```java
var request = merchant.getRequest("1jHXEbRTIbbwaoJ6w86");
```

| Argument | Description | Type |
| --- | --- | --- |
| `request_id` | The ID of transaction you wish to poll | `string`


### Have any doubts?
In case of any doubts, bugs, or the like, please leave an issue. We would love to help.

License
----------------

The library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
