package ao.vpos.vpos.model;

public class TransactionViewModel implements ViewModel<Transaction>{
    private final Integer statusCode;
    private final String message;
    private final Transaction data;

    public TransactionViewModel(Integer statusCode, String message, Transaction data) {
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
    public Transaction getData() {
        return data;
    }
}
