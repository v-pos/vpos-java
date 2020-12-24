package ao.vpos.vpos.model;


public class VposViewModel {
  private Integer code;
  private String message;
  private String data;

  public VposViewModel(Integer code, String message, String data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public Integer getCode() {
    return this.code;
  }

  public String getMessage() {
    return this.message;
  }

  public String getData() {
    return this.data;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return String.format("{\ncode: \"%s\",\nmessage: \"%s\",\ndata: %s\n}", code, message, data);
  }

}
