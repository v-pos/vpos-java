package ao.vpos.vpos;

import ao.vpos.vpos.model.RequestViewModel;
import ao.vpos.vpos.model.TransactionViewModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
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

        assertEquals(401, response.getCode());
        assertEquals("\"Unauthorized\"", response.getData());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfParentTransactionIdDoesNotExist() throws IOException, InterruptedException {

        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        var response = merchant.getTransaction(transactionId);

        assertEquals(404, response.getStatusCode());
        assertEquals("Not Found", response.getMessage());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.getTransaction(transactionId);

        assertEquals(401, response.getStatusCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfMobileIsInvalid() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("9001112223", "123.45");

        assertEquals(400, response.getCode());
        assertEquals("Bad Request", response.getMessage());
        assertEquals("{\"errors\":{\"mobile\":[\"has invalid format\"]}}", response.getData());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfAmountMalFormed() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45.36");

        assertEquals(400, response.getCode());
        assertEquals("Bad Request", response.getMessage());
        assertEquals("{\"errors\":{\"amount\":[\"is invalid\"]}}", response.getData());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.newPayment("900111222", "199.99");

        assertEquals(401, response.getCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    @Test
    public void itShouldNotCreateNewRefundTransactionIfParentTransactionIdDoesNotExist() throws IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();

        TimeUnit.SECONDS.sleep(10);

        var merchant = new Vpos();
        var response = merchant.newRefund(transactionId);
        assertEquals(202, response.getCode());

        var refundTransactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var transaction = merchant.getTransaction(refundTransactionId);

        assertEquals(404, transaction.getStatusCode());
        assertEquals("Not Found", transaction.getMessage());
    }

    @Test
    public void itShouldNotCreateNewRefundTransactionIfTokenIsInvalid() throws IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();

        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.newRefund(transactionId);

        assertEquals(401, response.getCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    // POSITIVES
    @Test
    public void itShouldGetSingleTransaction() throws IOException, InterruptedException {

        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var returnedResponse = (TransactionViewModel) merchant.getTransaction(transactionId);

        assertEquals(200, returnedResponse.getStatusCode());
        assertEquals("OK", returnedResponse.getMessage());
        assertEquals("123.45", returnedResponse.getData().getAmount());
        assertEquals("900111222", returnedResponse.getData().getMobile());
    }

    @Test
    public void itShouldGetAllTransactions() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.getTransactions();
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void itShouldCreateNewPaymentTransaction() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "99.45");

        assertEquals(202, response.getCode());
        assertEquals("Accepted", response.getMessage());
    }

    @Test
    public void itShouldCreateNewRefundTransaction() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var refundResponse = merchant.newRefund(transactionId);

        var returnedTransactionId = merchant.getTransactionId(refundResponse);
        var transactionResponse = merchant.getTransaction(returnedTransactionId);

        assertEquals(202, refundResponse.getCode());
        assertEquals(200, transactionResponse.getStatusCode());
    }

    @Test
    public void itShouldGetRequestWhileTransactionIsQueued() throws IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response);

        var requestResponse = (RequestViewModel) merchant.getRequest(transactionId);

        assertEquals(200, requestResponse.getStatusCode());
        assertNotNull(requestResponse.getData().getEta());
        assertNotNull(requestResponse.getData().getInsertedAt());
    }
}
