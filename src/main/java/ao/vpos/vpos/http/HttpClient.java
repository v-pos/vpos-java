package ao.vpos.vpos.http;

import ao.vpos.vpos.exceptions.VposSdkException;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HttpClient {
    public Response doRequest(Request request) {
        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder()
                .url(request.getUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");

        if (!request.getHeaders().isEmpty()) {
            request.getHeaders().forEach(okHttpRequestBuilder::addHeader);
        }

        RequestBody okHttpRequestBody = null;
        if (request.getBody().isPresent()) {
            okHttpRequestBody = RequestBody.create(
                    request.getBody().get(),
                    MediaType.get("application/json")
            );
        }

        switch (request.getMethod()) {
            case GET:
                okHttpRequestBuilder.get();
                break;

            case POST:
            case PATCH:
            case PUT:
                okHttpRequestBuilder.method(request.getMethod().name(), okHttpRequestBody);
                break;


            case DELETE:
                okHttpRequestBuilder.delete();
                break;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            okhttp3.Response okHttpResponse = okHttpClient.newCall(okHttpRequestBuilder.build()).execute();
            ResponseBody okHttpResponseBody = okHttpResponse.body();

            Map<String, Object> headers = new HashMap<>();
            okHttpResponse.headers()
                    .forEach(pair -> headers.put(pair.getFirst(), pair.getSecond()));
            Response.Builder responseBuilder = new Response.Builder()
                    .status(okHttpResponse.code())
                    .headers(headers);

            if (okHttpResponseBody != null) {
                responseBuilder
                        .bodyStream(okHttpResponseBody.byteStream())
                        .build();
            }

            return responseBuilder.build();
        } catch (IOException e) {
            throw new VposSdkException(e);
        }
    }
}
