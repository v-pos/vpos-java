package ao.vpos.vpos;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VposTest {
    // NEGATIVES
    @Test
    public void itShouldNotGetAllTransactionsIfTokenInvalid() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.getTransactions();

        assertEquals(401, response.getCode());
        assertEquals("\"Unauthorized\"", response.getData());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfParentTransactionIdDoesNotExist() throws MalformedURLException, IOException, InterruptedException {

        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        var response = merchant.getTransaction(transactionId);

        assertEquals(404, response.getCode());
        assertEquals("Not Found", response.getMessage());
    }

    @Test
    public void itShouldNotGetSingleTransactionIfTokenIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.getTransaction(transactionId);

        assertEquals(401, response.getCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfMobileIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("9001112223", "123.45");

        assertEquals(400, response.getCode());
        assertEquals("Bad Request", response.getMessage());
        assertEquals("{\"errors\":{\"mobile\":[\"has invalid format\"]}}", response.getData());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfAmountMalFormed() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45.36");

        assertEquals(400, response.getCode());
        assertEquals("Bad Request", response.getMessage());
        assertEquals("{\"errors\":{\"amount\":[\"is invalid\"]}}", response.getData());
    }

    @Test
    public void itShouldNotCreateNewPaymentTransactionIfTokenIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.newPayment("900111222", "199.99");

        assertEquals(401, response.getCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    @Test
    public void itShouldNotCreateNewRefundTransactionIfParentTransactionIdDoesNotExist() throws MalformedURLException, IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();

        TimeUnit.SECONDS.sleep(10);

        var merchant = new Vpos();
        var response = merchant.newRefund(transactionId);
        assertEquals(202, response.getCode());

        var refundTransactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var transaction = merchant.getTransaction(refundTransactionId);

        assertEquals(404, transaction.getCode());
        assertEquals("Not Found", transaction.getMessage());
    }

    @Test
    public void itShouldNotCreateNewRefundTransactionIfTokenIsInvalid() throws MalformedURLException, IOException, InterruptedException {
        var transactionId = UUID.randomUUID().toString();

        var merchant = new Vpos();
        merchant.setToken("invalid-token");
        var response = merchant.newRefund(transactionId);

        assertEquals(401, response.getCode());
        assertEquals("Unauthorized", response.getMessage());
    }

    // POSITIVES
    @Test
    public void itShouldGetSingleTransaction() throws MalformedURLException, IOException, InterruptedException {

        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var returnedResponse = merchant.getTransaction(transactionId);

        assertEquals(200, returnedResponse.getCode());
        assertEquals("OK", returnedResponse.getMessage());
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
    public void itShouldCreateNewPaymentTransaction() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "99.45");

        assertEquals(202, response.getCode());
        assertEquals("Accepted", response.getMessage());
    }

    @Test
    public void itShouldCreateNewRefundTransaction() throws MalformedURLException, IOException, InterruptedException {
        var merchant = new Vpos();
        var response = merchant.newPayment("900111222", "123.45");
        var transactionId = merchant.getTransactionId(response);

        TimeUnit.SECONDS.sleep(10);

        var refundResponse = merchant.newRefund(transactionId);

        var returnedTransactionId = merchant.getTransactionId(refundResponse);
        var transactionResponse = merchant.getTransaction(returnedTransactionId);

        assertEquals(202, refundResponse.getCode());
        assertEquals(200, transactionResponse.getCode());

    }
}
