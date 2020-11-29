package ao.vpos.vpos.exceptions;

import ao.vpos.vpos.http.Response;
import ao.vpos.vpos.json.JsonSerializer;

public class VposApiException extends VposSdkException {
  public static VposApiException fromResponse(Response response, JsonSerializer jsonSerializer) {
    return new VposApiException(response.getStatus());
  }

  private final Integer status;

  public VposApiException(Integer status) {
    this.status = status;
  }

  public Integer getStatus() {
    return status;
  }
}
