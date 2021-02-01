package ao.vpos.vpos;

import ao.vpos.vpos.model.*;
import co.ao.nextbss.Yoru;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.UUID;

public class Vpos {
  private static final String PRODUCTION_BASE_URL = "https://api.vpos.ao";
  private static final String SANDBOX_BASE_URL = "https://sandbox.vpos.ao";

  private final URL baseUrl;
  private String token;

  private static final int BEGIN_LOCATION_INDEX = 18;

  public enum Environment {
    PRODUCTION,
    SANDBOX
  }

  // constructors
  public Vpos() throws IOException, InterruptedException {
      try{
        this.token = System.getenv("MERCHANT_VPOS_TOKEN");
        this.baseUrl = new URL(SANDBOX_BASE_URL);
      } catch(MalformedURLException e) {
          throw new RuntimeException(e);
      }
  }

  public Vpos(Environment environment) throws IOException, InterruptedException  {
    try{
      this.token = System.getenv("MERCHANT_VPOS_TOKEN");
      this.baseUrl = (environment == Environment.SANDBOX) ? new URL(PRODUCTION_BASE_URL) : new URL(SANDBOX_BASE_URL);
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public Vpos(String newToken) throws IOException, InterruptedException  {
    try{
      this.token = newToken;
      this.baseUrl = new URL(SANDBOX_BASE_URL);
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public Vpos(String newToken, Environment environment) throws IOException, InterruptedException {
    try{
      this.token = newToken;
      this.baseUrl = (environment == Environment.SANDBOX) ? new URL(PRODUCTION_BASE_URL) : new URL(SANDBOX_BASE_URL);
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  // api methods
  public VposResponse getTransactions() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return returnObject(response);
  }

  public BaseResponse getTransaction(String transactionId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/transactions/%s", transactionId)))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      Yoru<TransactionResponse> converter = new Yoru();
      TransactionResponse transactionResponse = converter.fromJson(response.body(), TransactionResponse.class);
      return new Transaction(200, "OK", transactionResponse);
    }
    return returnObject(response);
  }

  // api payment methods
  public VposResponse newPayment(String mobile, String amount) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    var body = new HashMap<>();

    body.put("type", "payment");
    body.put("pos_id", System.getenv("GPO_POS_ID"));
    body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposResponse newPayment(String mobile, String amount, String posID) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    var body = new HashMap<>();

    body.put("type", "payment");
    body.put("pos_id", posID);
    body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposResponse newPayment(String mobile, String amount, String posID, String paymentCallbackUrl) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    var body = new HashMap<>();

    body.put("type", "payment");
    body.put("pos_id", posID);
    body.put("callback_url", paymentCallbackUrl);
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    var request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  // api refund methods
  public VposResponse newRefund(String parentTransactionId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", System.getenv("GPO_SUPERVISOR_CARD"));
    body.put("callback_url", System.getenv("VPOS_REFUND_CALLBACK_URL"));
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    var requestBody = objectMapper.writeValueAsString(body);

    var request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposResponse newRefund(String parentTransactionId, String supervisorCard) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", supervisorCard);
    body.put("callback_url", System.getenv("VPOS_REFUND_CALLBACK_URL"));
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposResponse newRefund(String parentTransactionId, String supervisorCard, String refundCallbackUrl) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", supervisorCard);
    body.put("callback_url", refundCallbackUrl);
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  // api poll status methods
  public BaseResponse getRequest(String requestId) throws IOException, InterruptedException {
    var client = HttpClient.newHttpClient();

    var request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/requests/%s", requestId)))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      Yoru<RequestResponse> converter = new Yoru<>();
      var requestResponse = converter.fromJson(response.body(), RequestResponse.class);
      return new Request(200, "OK", requestResponse, null);
    }

    if (response.statusCode() == 303) {
      return new Request(303, "See More", null, getLocation(response));
    }

    return returnObject(response);
  }

  private String getLocation(HttpResponse<String> response) {
    return response.headers().map().get("Location").toString();
  }

  // Helpers
  private VposResponse returnObject(HttpResponse<String> response) throws JsonProcessingException {
    switch(response.statusCode()) {
      case 200:
        return new VposResponse(response.statusCode(), "OK", response.body());
      case 202:
        return new VposResponse(response.statusCode(), "Accepted", response.headers().map().get("Location").toString());
      case 303:
        return new VposResponse(response.statusCode(), "See More", response.headers().map().get("Location").toString());
      case 404:
        return new VposResponse(response.statusCode(), "Not Found", "Empty");
      case 400:
        return new VposResponse(response.statusCode(), "Bad Request", response.body());
      case 401:
        return new VposResponse(response.statusCode(), "Unauthorized", response.body());
      default:
        return new VposResponse(response.statusCode(), "Unknown Error", "Please contact administrator for help");
    }
  }

  private String getToken() {
    return this.token;
  }

  protected void setToken(String token) {
    this.token = token;
  }

  private String getBaseUrl() {
    return String.valueOf(this.baseUrl);
  }

  public String getTransactionId(VposResponse object) throws IOException, InterruptedException {
      var location = object.getData();
      var endLocationIndex = location.length() - 1;
      return location.substring(BEGIN_LOCATION_INDEX, endLocationIndex);
  }
}
