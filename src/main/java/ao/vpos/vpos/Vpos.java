package ao.vpos.vpos;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ao.vpos.vpos.model.ReturnObject;
import javax.annotation.Nonnull;

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

  private String getToken() {
    return token; 
  }

  private String getBaseUrl() {
    return String.valueOf(baseUrl);
  }

  public HashMap<String, String> getAllTransactions() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + token)
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    HashMap<String, String> returnedObject = new HashMap<String, String>();

    returnedObject.put("code", String.valueOf(response.statusCode()));
    returnedObject.put("message", "OK"); 
    returnedObject.put("data", String.valueOf(response.body()));

    return returnedObject;
  }

  public ReturnObject getTransaction(String transactionId) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + token)
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/transactions/%s", transactionId)))
      .build();
    
    //HashMap<String, String> returnedObject = new HashMap<String, String>();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    //returnedObject.put("code", String.valueOf(response.statusCode()));
    //returnedObject.put("message", "OK"); 
    //returnedObject.put("data", response.body());

    ReturnObject returnedObject = new ReturnObject(response.statusCode(), "OK", response.body());

    return returnedObject; 
  }
}
