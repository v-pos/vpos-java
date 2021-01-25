package ao.vpos.vpos.model;

public class Transaction implements ViewModel<TransactionResponse>{
    private final Integer statusCode;
    private final String message;
    private final TransactionResponse data;

    public Transaction(Integer statusCode, String message, TransactionResponse data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TransactionResponse getData() {
        return data;
    }
}
