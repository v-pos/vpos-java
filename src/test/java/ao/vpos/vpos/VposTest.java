/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ao.vpos.vpos;

import ao.vpos.vpos.model.VposViewModel;
import ao.vpos.vpos.Vpos;
import ao.vpos.vpos.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class VposTest {
    // NEGATIVES
    @Test
    public void itShouldNotGetAllTransactionsIfTokenInvalid() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKE");
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var transactions = returnObject(response);
        
        assertEquals(401, transactions.getCode());
        assertEquals("Not Authorized", transactions.getMessage());
    }
    
    @Test
    public void itShouldNotGetSingleTransactionIfParentTransactionIdDoesNotExist() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKEN");
        var transactionId = UUID.randomUUID().toString();
                
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions/" + transactionId))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(404, returnedResponse.statusCode());
        assertEquals("\"Not Found\"", returnedResponse.body());
    }
    
    @Test
    public void itShouldNotGetSingleTransactionIfTokenIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKEN");
        var response = VposBuilder.newPayment("900111222", "123.45", token);
        var location = response.headers().map().get("Location").toString();
        var transactionId = location.substring(18, location.length() - 1);
                
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + UUID.randomUUID().toString())
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions/" + transactionId))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(401, returnedResponse.statusCode());
    }
    
    // POSITIVES
    @Test
    public void itShouldGetSingleTransaction() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKEN");
        var response = VposBuilder.newPayment("900111222", "123.45", token);
        var location = response.headers().map().get("Location").toString();
        var transactionId = location.substring(18, location.length() - 1);
        
        TimeUnit.SECONDS.sleep(10);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions/" + transactionId))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        Transaction transaction = objectMapper.readValue(returnedResponse.body(), Transaction.class);
        
        assertEquals(200, returnedResponse.statusCode());
        assertEquals(transactionId, transaction.getId());
    }
    
    @Test
    public void itShouldGetAllTransactions() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKEN");
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var transactions = returnObject(response);
        
        assertEquals(200, transactions.getCode());
        assertEquals("OK", transactions.getMessage());
    }
    
    // Helpers
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
      case 401:
        return new VposViewModel(response.statusCode(), "Not Authorized", response.body());
      default:
        return new VposViewModel(response.statusCode(), "Unknown Error", "Please contant administrator for help");
    } 
  }
}
