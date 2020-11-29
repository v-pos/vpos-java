package ao.vpos.vpos;

import com.google.common.base.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;

public class Vpos {
  private static final String PRODUCTION_BASE_URL = "https://api.vpos.ao";
  private static final String SANDBOX_BASE_URL = "https://sandbox.vpos.ao";

  private final URL baseUrl;
  private final String token;

  public enum Environment {
    PRODUCTION,
    SANDBOX
  }

  public static class Builder {
    private URL baseUrl;
    private Environment environment;
    private String token;
    private Integer posId;
    private String supervisorCard; 

    @Nonnull
    public Builder posId(@Nonnull Integer posId) {
      //Preconditions.checkNotNull(baseUrl, "posId can't be null");

      this.posId = posId;
      return this;
    }

    public Builder supervisorCard(@Nonnull String supervisorCard) {
      //Preconditions.checkNotNull(supervisorCard, "supervisorCard can't be null");

      this.supervisorCard = supervisorCard;
      return this;
    }

    @Nonnull
    public Builder baseUrl(@Nonnull String baseUrl) {
      //Preconditions.checkNotNull(baseUrl, "baseUrl can't be null");

      try {
        this.baseUrl = new URL(baseUrl);

        return this;
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }

    public Builder environment(Environment environment) {
      if (environment == null) {
        throw new IllegalArgumentException("environment can't be null");
      }

      this.environment = environment;
      return this;
    }

    public Builder token(String token) {
      //Preconditions.checkNotNull(token, "token can't be null");

      this.token = token;
      return this;
    }

    public Vpos build() {
      if (environment == null && baseUrl == null) {
        throw new IllegalArgumentException("vPOS baseUrl is required when environment is not specified");
      }

      if (baseUrl != null && baseUrl.toString().endsWith("/")) {
        baseUrl = removeTrailingSlashFromUrl(baseUrl);
      }

      if (environment != null) {
        baseUrl = getBaseUrlByEnvironment(environment);
      }

      return new Vpos(baseUrl, token);
    }

    @SuppressWarnings("unused")
    private URL getBaseUrlByEnvironment(Environment environment) {
      try {
        if (environment == environment.PRODUCTION) {
          return new URL(PRODUCTION_BASE_URL);
        } else {
          return new URL(SANDBOX_BASE_URL);
        }
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }

    private URL removeTrailingSlashFromUrl(URL baseUrl) {
      String baseUrlString = baseUrl.toString();
      if (!baseUrlString.endsWith("/")) {
        return baseUrl;
      }

      try {
        int indexOfTrailingSlash = baseUrlString.lastIndexOf("/");
        return new URL(baseUrlString.substring(0, indexOfTrailingSlash));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  Vpos(URL baseUrl, String token) {
    if (baseUrl == null) {
      throw new IllegalArgumentException("vPOS baseUrl is required");
    }

    this.baseUrl = baseUrl;
    this.token = token;
  }

  /**
   * Retrieve vPOS base URL
   */
  public URL getBaseUrl() {
    return baseUrl;
  }

  /**
   * Retrieve vPOS admin authentication token.
   */
  public String getToken() {
    return token;
  }
}
