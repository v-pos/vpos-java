package ao.vpos.vpos.http;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Request {
    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    private final HttpMethod method;
    private final URL url;
    private final String body;
    private final Map<String, String> headers = new HashMap<>();

    public Request(HttpMethod method, String url) {
        this(method, url, null);
    }

    public Request(HttpMethod method, String url, String body) {
        Preconditions.checkNotNull(method);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url));

        try {
            this.method = method;
            this.url = new URL(url);
            this.body = body;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Request addHeader(String header, String value) {
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(value);

        headers.put(header, value);
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
