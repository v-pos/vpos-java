package ao.vpos.vpos.exception;

import java.net.http.HttpHeaders;

public class ApiException extends Exception {
    private final String message;
    private final int status;
    private HttpHeaders headers;
    private String responseBody;

    public ApiException(int status, String message) {
        this.status = status;
        this.message = message;
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
