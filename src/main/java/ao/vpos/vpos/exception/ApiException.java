package ao.vpos.vpos.exception;

import java.net.http.HttpHeaders;

public class ApiException extends Exception {
    private String message;
    private int status;
    private HttpHeaders headers;
    private String responseBody;

    public ApiException(int status, String responseBody) {
        this.status = status;
        this.responseBody = responseBody;
    }

    public ApiException(int status, String message, String responseBody) {
        this.status = status;
        this.message = message;
        this.responseBody = responseBody;
    }

    public ApiException(String message, int status, HttpHeaders headers, String responseBody) {
        this.message = message;
        this.status = status;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
