package ao.vpos.vpos.model;

public class Request implements BaseResponse<RequestResponse>, LocationViewModel {
    private final Integer statusCode;
    private final String message;
    private final RequestResponse data;
    private final String location;

    public Request(Integer statusCode, String message, RequestResponse data, String location) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.location = location;
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

    @Override
    public String getLocation() {
        return location;
    }
}
