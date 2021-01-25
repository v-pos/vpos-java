package ao.vpos.vpos.model;

public class RequestViewModel implements ViewModel<RequestResponse> {
    private final Integer statusCode;
    private final String message;
    private final RequestResponse data;

    public RequestViewModel(Integer statusCode, String message, RequestResponse data) {
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
    public RequestResponse getData() {
        return data;
    }
}
