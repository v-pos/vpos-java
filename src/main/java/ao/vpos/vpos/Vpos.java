package ao.vpos.vpos;

import ao.vpos.vpos.exception.ApiException;
import ao.vpos.vpos.model.Request;
import ao.vpos.vpos.model.Response;
import ao.vpos.vpos.model.Transaction;
import co.ao.nextbss.Yoru;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Vpos {
  private static final String HOST = "https://vpos.ao";
  private final HttpClient client = HttpClient.newHttpClient();

  private URL baseUrl;
  private String token;
  private String posId;
  private String supervisorCard;
  private String paymentCallbackUrl;
  private String refundCallbackUrl;

  private static final int BEGIN_LOCATION_INDEX = 18;

  // constructors
  public Vpos() throws MalformedURLException {
    this.token = System.getenv("MERCHANT_VPOS_TOKEN");
    this.posId = System.getenv("GPO_POS_ID");
    this.supervisorCard = System.getenv("GPO_SUPERVISOR_CARD");
    this.paymentCallbackUrl = System.getenv("PAYMENT_CALLBACK_URL");
    this.refundCallbackUrl = System.getenv("REFUND_CALLBACK_URL");
    this.baseUrl = new URL(HOST);
  }

  public Vpos(@Nonnull String newToken) throws MalformedURLException {
    this();
    this.token = newToken;
    this.baseUrl = new URL(HOST);
  }

  public Vpos(@Nonnull String newToken, @Nonnull String posId) throws MalformedURLException {
    this(newToken);
    this.posId = posId;
    this.baseUrl = new URL(HOST);
  }

  public Vpos(
          @Nonnull String newToken,
          @Nonnull String posId,
          @Nonnull String supervisorCard) throws MalformedURLException {
    this(newToken, posId);
    this.supervisorCard = supervisorCard;
  }

  public Vpos(
          @Nonnull String newToken,
          @Nonnull String posId,
          @Nonnull String supervisorCard,
          @Nonnull String paymentCallbackUrl) throws MalformedURLException {
    this(newToken, posId, supervisorCard);
    this.paymentCallbackUrl = paymentCallbackUrl;
  }

  public Vpos(
          @Nonnull String newToken,
          @Nonnull String posId,
          @Nonnull String supervisorCard,
          @Nonnull String paymentCallbackUrl,
          @Nonnull String refundCallbackUrl) throws MalformedURLException {
    this(newToken, posId, supervisorCard, paymentCallbackUrl);
    this.refundCallbackUrl = refundCallbackUrl;
  }

  // api methods
  public Response<List<Transaction>> getTransactions() throws IOException, InterruptedException, ApiException {
    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      return new Response<List<Transaction>>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "OK";
        }

        @Override
        public List<Transaction> getData() throws IOException {
          CollectionType typeReference =
                  TypeFactory.defaultInstance().constructCollectionType(List.class, Transaction.class);
          ObjectMapper objectMapper = new ObjectMapper();
          return objectMapper.readValue(response.body(), typeReference);
        }

        @Override
        public String getLocation() {
          return null;
        }
      };
    } else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  public Response<Transaction> getTransaction(String transactionId) throws IOException, InterruptedException, ApiException {
    HttpRequest request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/transactions/%s", transactionId)))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      Yoru<Transaction> converter = new Yoru();
      Transaction transaction = converter.fromJson(response.body(), Transaction.class);

      return new Response<Transaction>() {

        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "OK";
        }

        @Override
        public Transaction getData() {
          return transaction;
        }

        @Override
        public String getLocation() {
          return null;
        }
      };
    } else {
        throw new ApiException(response.statusCode(), response.body());
    }
  }

  // api payment methods
  public Response newPayment(String mobile, String amount) throws IOException, InterruptedException, ApiException {
    var body = new HashMap<>();
    body.put("type", "payment");
    body.put("pos_id", posId);
    body.put("callback_url", paymentCallbackUrl);
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 202) {
      return new Response<String>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public String getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    }
    else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  public Response newPayment(String mobile, String amount, String posID) throws IOException, InterruptedException, ApiException {
    var body = new HashMap<>();
    body.put("type", "payment");
    body.put("pos_id", posID);
    body.put("callback_url", paymentCallbackUrl);
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 202) {
      return new Response<String>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public String getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    }
    else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  public Response newPayment(String mobile, String amount, String posID, String paymentCallbackUrl) throws IOException, InterruptedException, ApiException {
    var body = new HashMap<>();
    body.put("type", "payment");
    body.put("pos_id", posID);
    body.put("callback_url", paymentCallbackUrl);
    body.put("mobile", mobile);
    body.put("amount", amount);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    var request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 202) {
      return new Response<String>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public String getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    }
    else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  // api refund methods
  public Response newRefund(String parentTransactionId) throws IOException, InterruptedException, ApiException {
    var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", supervisorCard);
    body.put("callback_url", refundCallbackUrl);
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    var requestBody = objectMapper.writeValueAsString(body);

    var request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 202) {
      return new Response() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public Object getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    } else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  public Response newRefund(String parentTransactionId, String supervisorCard) throws IOException, InterruptedException, ApiException {
    var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", supervisorCard);
    body.put("callback_url", refundCallbackUrl);
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 202) {
      return new Response() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public Object getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    } else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  public Response newRefund(String parentTransactionId, String supervisorCard, String refundCallbackUrl) throws IOException, InterruptedException, ApiException {
   var body = new HashMap<>();
    body.put("type", "refund");
    body.put("supervisor_card", supervisorCard);
    body.put("callback_url", refundCallbackUrl);
    body.put("parent_transaction_id", parentTransactionId);

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(body);

    HttpRequest request = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .header("Idempotency-Key", UUID.randomUUID().toString())
      .uri(URI.create(getBaseUrl() + "/api/v1/transactions"))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 202) {
      return new Response() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "Accepted";
        }

        @Override
        public Object getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    } else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  // api poll status methods
  public Response<Request> getRequest(String requestId) throws IOException, InterruptedException, ApiException {
    var request = HttpRequest.newBuilder()
      .GET()
      .header("Accept", "application/json")
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + getToken())
      .uri(URI.create(getBaseUrl() + String.format("/api/v1/requests/%s", requestId)))
      .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 200) {
      Yoru<Request> converter = new Yoru<>();
      var requestResponse = converter.fromJson(response.body(), Request.class);
      return new Response<Request>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "OK";
        }

        @Override
        public Request getData() {
          return requestResponse;
        }

        @Override
        public String getLocation() {
          return null;
        }
      };
    }

    if (response.statusCode() == 303) {
      return new Response<Request>() {
        @Override
        public Integer getStatusCode() {
          return response.statusCode();
        }

        @Override
        public String getMessage() {
          return "See More";
        }

        @Override
        public Request getData() {
          return null;
        }

        @Override
        public String getLocation() {
          return response.headers().map().get("Location").toString();
        }
      };
    } else {
      throw new ApiException(response.statusCode(), response.body());
    }
  }

  private String getLocation(HttpResponse<String> response) {
    return response.headers().map().get("Location").toString();
  }

  private String getToken() {
    return this.token;
  }

  protected void setToken(String token) {
    this.token = token;
  }

  private String getBaseUrl() {
    return String.valueOf(this.baseUrl);
  }

  public String getTransactionId(String location) throws IOException, InterruptedException {
    var endLocationIndex = location.length() - 1;
    return location.substring(BEGIN_LOCATION_INDEX, endLocationIndex);
  }
}
