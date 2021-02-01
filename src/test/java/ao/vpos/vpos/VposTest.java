package ao.vpos.vpos;

import ao.vpos.vpos.exception.ApiException;
import ao.vpos.vpos.model.Response;
import ao.vpos.vpos.model.Request;
import ao.vpos.vpos.model.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class VposTest {
    // NEGATIVES
    @Test
    public void itShouldNotGetAllTransactionsIfTokenInvalid() throws IOException, InterruptedException {
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.getTransactions();

        assertEquals(401, response.getStatusCode());
        assertEquals("\"Unauthorized\"", response.getData());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfParentTransactionIdDoesNotExist() throws IOException, InterruptedException {

        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        ApiException exception = null;
        try {
            Response<Transaction> response = merchant.getTransaction(transactionId);
        } catch (ApiException e) {
            exception = e;
        }

        assertNotNull(exception);
        assertEquals(404, exception.getStatus());
        assertEquals(null, exception.getResponseBody());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        ApiException exception = null;
        try {
            Response<Transaction> response = merchant.getTransaction(transactionId);
        } catch (ApiException e) {
            exception = e;
        }

        assertEquals(401, exception.getStatus());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfMobileIsInvalid() throws IOException, InterruptedException {
        var merchant = new Vpos();
        Response<String> response = null;
        ApiException exception = null;
        try {
            response = merchant.newPayment("9001112223", "123.45");
        } catch (ApiException e) {
            exception = e;
        }

        assertEquals(400, exception.getStatus());
        assertEquals("{\"errors\":{\"mobile\":[\"has invalid format\"]}}", exception.getMessage());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfAmountMalFormed() throws IOException, InterruptedException {
        var merchant = new Vpos();
        Response<String> response = null;
        ApiException exception = null;
        try {
            response = merchant.newPayment("900111222", "123.45.36");
        } catch (ApiException e) {
            exception = e;
        }

        assertEquals(400, exception.getStatus());
        assertEquals("{\"errors\":{\"amount\":[\"is invalid\"]}}", exception.getMessage());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        Response<String> response = null;
        ApiException exception = null;
        try {
            response = merchant.newPayment("900111222", "199.99");
        } catch (ApiException e) {
            exception = e;
        }

        assertEquals(401, exception.getStatus());
    }

    @Test
    public void itShouldNotCreateNewRefundTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var transactionId = "non-existent-transaction-id";

        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        ApiException exception = null;
        Response response = null;
        try {
            response = merchant.newRefund(transactionId);
        } catch (ApiException e) {
            exception = e;
        }

        assertEquals(401, exception.getStatus());
        assertEquals("\"Unauthorized\"", exception.getMessage());
    }

    // POSITIVES
    @Test
    public void itShouldGetSingleTransaction() throws IOException, InterruptedException, ApiException {

        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response.getLocation());

        TimeUnit.SECONDS.sleep(10);

        var returnedResponse = merchant.getTransaction(transactionId);

        assertEquals(200, returnedResponse.getStatusCode());
        assertEquals("OK", returnedResponse.getMessage());
        assertEquals("123.45", returnedResponse.getData().getAmount());
        assertEquals("900111222", returnedResponse.getData().getMobile());
    }

    @Test
    public void itShouldGetAllTransactions() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.getTransactions();
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void itShouldCreateNewPaymentTransaction() throws IOException, InterruptedException, ApiException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "99.45");

        assertEquals(202, response.getStatusCode());
        assertEquals("Accepted", response.getMessage());
    }

    @Test
    public void itShouldCreateNewRefundTransaction() throws IOException, InterruptedException, ApiException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response.getLocation());

        TimeUnit.SECONDS.sleep(10);

        var refundResponse = merchant.newRefund(transactionId);

        var id = merchant.getTransactionId(refundResponse.getLocation());

        var transactionResponse = merchant.getTransaction(id);

        assertEquals(202, refundResponse.getStatusCode());
        assertEquals(200, transactionResponse.getStatusCode());
    }

    @Test
    public void itShouldGetRequestWhileTransactionIsQueued() throws IOException, InterruptedException, ApiException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response.getLocation());

        Response<Request> request = merchant.getRequest(transactionId);

        assertEquals(200, request.getStatusCode());
        assertNotNull(request.getData().getEta());
        assertNotNull(request.getData().getInsertedAt());
    }

    @Test
    public void itShouldGetRequestWhenTransactionIsComplete() throws IOException, InterruptedException, ApiException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response.getLocation());

        TimeUnit.SECONDS.sleep(10);

        var request = merchant.getRequest(transactionId);

        assertEquals(303, request.getStatusCode());
        assertNotNull(request.getLocation());
        assertNull(request.getData());
    }
}
