package ao.vpos.vpos.http;

import com.google.common.base.Preconditions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final int status;
    private final InputStream bodyStream;
    private final Map<String, Object> headers;

    private Response(int status, InputStream bodyStream, Map<String, Object> headers) {
        Preconditions.checkNotNull(headers);
        this.status = status;
        this.bodyStream = bodyStream;
        this.headers = normalizeHeadersKeys(headers);
    }

    public int getStatus() {
        return status;
    }

    public InputStream getBodyStream() {
        return bodyStream;
    }

    public <T> T getHeaderValue(String key) {
        Object value = headers.get(key.toLowerCase());
        if (value == null) {
            throw new RuntimeException(String.format("There's no key '%s' in the headers", key));
        }

        //noinspection unchecked
        return (T) value;
    }

    public boolean isSuccessful() {
        return status >= 200 && status <= 299;
    }

    private Map<String, Object> normalizeHeadersKeys(Map<String, Object> headers) {
        Map<String, Object> normalizedHeader = new HashMap<>();
        headers.forEach((key, value) -> normalizedHeader.put(key.toLowerCase(), value));
        return normalizedHeader;
    }

    static class Builder {
        private int status;
        private InputStream bodyStream;
        private Map<String, Object> headers = new HashMap<>();

        public Builder() {}

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder bodyStream(InputStream bodyStream) {
            this.bodyStream = bodyStream;
            return this;
        }

        public Builder headers(Map<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        public Response build() {
            return new Response(status, bodyStream, headers);
        }
    }
}
