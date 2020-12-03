package ao.vpos.vpos;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ao.vpos.vpos.model.VposViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.UUID;

public class Vpos {
  private static final String PRODUCTION_BASE_URL = "https://api.vpos.ao";
  private static final String SANDBOX_BASE_URL = "https://sandbox.vpos.ao";

  private final URL baseUrl;
  private final String token;

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
  public VposViewModel getTransactions() throws IOException, InterruptedException {
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

  public VposViewModel getTransaction(String transactionId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/transactions/%s", transactionId)))
      .build();
    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposViewModel newPayment(HashMap<String, String> body) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    body.put("type", "payment");
    body.put("pos_id", System.getenv("GPO_POS_ID"));
    body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));

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

  public VposViewModel newRefund(HashMap<String, String> body) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    body.put("type", "refund");
    body.put("supervisor_card", System.getenv("GPO_SUPERVISOR_CARD"));
    body.put("callback_url", System.getenv("VPOS_REFUND_CALLBACK_URL"));

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

  public VposViewModel getRequest(String requestId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/requests/%s", requestId)))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  // helper methods
  private VposViewModel returnObject(HttpResponse<String> response) throws JsonProcessingException {
    switch(response.statusCode()) {
      case 200:
        return new VposViewModel(response.statusCode(), "OK", response.body());
      case 202:
        return new VposViewModel(response.statusCode(), "Accepted", response.headers().map().get("Location").toString());
      case 303:
        return new VposViewModel(response.statusCode(), "See More", response.headers().map().get("Location").toString());
      case 404:
        return new VposViewModel(response.statusCode(), "Not Found", "Empty");
      case 400:
        return new VposViewModel(response.statusCode(), "Bad Request", response.body());
      default:
        return new VposViewModel(response.statusCode(), "Unknown Error", "Please contant administrator for help");
    } 
  }

  private String getToken() {
    return this.token;
  }

  private String getBaseUrl() {
    return String.valueOf(this.baseUrl);
  }

  public String getTransactionId(VposViewModel object) throws IOException, InterruptedException {
      String location = object.getData().toString();
      return location.substring(18, location.length() - 1);
  }
}
