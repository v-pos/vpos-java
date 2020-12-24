package ao.vpos.vpos;

import ao.vpos.vpos.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
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
        
        assertEquals(401, response.statusCode());
        assertEquals("\"Unauthorized\"", response.body());
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
        assertEquals("\"Unauthorized\"", returnedResponse.body());
    }
    
    @Test
    public void itShouldNotCreateNewPaymentTransactionIfMobileIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        
        var body = new HashMap<String, String>();
    
        body.put("type", "payment");
        body.put("pos_id", System.getenv("GPO_POS_ID"));
        body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
        body.put("mobile", "9001112223");
        body.put("amount", "123.45");
        
        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(body);
        
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("MERCHANT_VPOS_TOKEN"))
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, returnedResponse.statusCode());
        assertEquals("{\"errors\":{\"mobile\":[\"has invalid format\"]}}", returnedResponse.body());
    }
    
    @Test
    public void itShouldNotCreateNewPaymentTransactionIfAmountMalFormated() throws MalformedURLException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        
        var body = new HashMap<String, String>();
    
        body.put("type", "payment");
        body.put("pos_id", System.getenv("GPO_POS_ID"));
        body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
        body.put("mobile", "900111222");
        body.put("amount", "123.45.12");
        
        var objectMapper = new ObjectMapper();
        var requestBody = objectMapper.writeValueAsString(body);
        
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("MERCHANT_VPOS_TOKEN"))
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(400, returnedResponse.statusCode());
        assertEquals("{\"errors\":{\"amount\":[\"is invalid\"]}}", returnedResponse.body());
    }
    
    @Test
    public void itShouldNotCreateNewPaymentTransactionIfTokenIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        
        var body = new HashMap<String, String>();
    
        body.put("type", "payment");
        body.put("pos_id", System.getenv("GPO_POS_ID"));
        body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
        body.put("mobile", "900111222");
        body.put("amount", "123.45.12");
        
        var objectMapper = new ObjectMapper();
        var requestBody = objectMapper.writeValueAsString(body);
        
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + UUID.randomUUID().toString())
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(401, returnedResponse.statusCode());
        assertEquals("\"Unauthorized\"", returnedResponse.body());
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
        
        assertEquals(200, response.statusCode());
    }
    
    @Test
    public void itShouldCreateNewPaymentTransaction() throws MalformedURLException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        
        var body = new HashMap<String, String>();
    
        body.put("type", "payment");
        body.put("pos_id", System.getenv("GPO_POS_ID"));
        body.put("callback_url", System.getenv("VPOS_PAYMENT_CALLBACK_URL"));
        body.put("mobile", "900111222");
        body.put("amount", "123.45");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(body);
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("MERCHANT_VPOS_TOKEN"))
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(202, returnedResponse.statusCode());
    }

    @Test
    public void itShouldCreateNewRefundTransaction() throws MalformedURLException, IOException, InterruptedException {
        var token = System.getenv("MERCHANT_VPOS_TOKEN");
        var response = VposBuilder.newPayment("900111222", "123.45", token);
        var location = response.headers().map().get("Location").toString();
        var transactionId = location.substring(18, location.length() - 1);

        TimeUnit.SECONDS.sleep(10);

        HttpClient client = HttpClient.newHttpClient();

        var body = new HashMap<String, String>();

        body.put("type", "refund");
        body.put("supervisor_card", System.getenv("GPO_SUPERVISOR_CARD"));
        body.put("callback_url", System.getenv("VPOS_REFUND_CALLBACK_URL"));
        body.put("parent_transaction_id", transactionId);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + System.getenv("MERCHANT_VPOS_TOKEN"))
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .uri(URI.create(new URL("https://sandbox.vpos.ao") + "/api/v1/transactions"))
                .build();
        HttpResponse<String> returnedResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        var returnedLocation = returnedResponse.headers().map().get("Location").toString();
        var returnedTransactionId = returnedLocation.substring(18, returnedLocation.length() - 1);
        var transaction = VposBuilder.getTransaction(returnedTransactionId, token);

        assertEquals(202, returnedResponse.statusCode());
        assertEquals(transactionId, transaction.getParentTransactionId());
    }
}
