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

public class Vpos {
  private static final String PRODUCTION_BASE_URL = "https://api.vpos.ao";
  private static final String SANDBOX_BASE_URL = "https://sandbox.vpos.ao";

  private final URL baseUrl;
  private final String token;

  public enum Environment {
    PRODUCTION,
    SANDBOX
  }

  public Vpos(String token) throws IOException, InterruptedException  {
    this.token = token;
    try{
      this.baseUrl = new URL(SANDBOX_BASE_URL);
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public Vpos(Environment environment, String token) throws IOException, InterruptedException {
    try{
      this.baseUrl = (environment == Environment.SANDBOX) ? new URL(PRODUCTION_BASE_URL) : new URL(SANDBOX_BASE_URL);
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
    this.token = token;
  }

  public VposViewModel getAllTransactions() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + token)
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    VposViewModel returnedObject = new VposViewModel(response.statusCode(), "OK", response.body());

    return returnedObject;
  }

  public VposViewModel getTransaction(String transactionId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + token)
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/transactions/%s", transactionId)))
      .build();
    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  public VposViewModel newPayment(HashMap<String, String> body) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + token)
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();
    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    return returnObject(response);
  }

  private VposViewModel returnObject(HttpResponse<String> response) throws JsonProcessingException{
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
    return token; 
  }

  private String getBaseUrl() {
    return String.valueOf(baseUrl);
  }
}
