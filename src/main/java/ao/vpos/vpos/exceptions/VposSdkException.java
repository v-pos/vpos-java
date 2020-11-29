package ao.vpos.vpos.exceptions;

public class VposSdkException extends RuntimeException {
  public VposSdkException() { }

  public VposSdkException(Throwable cause) {
    super(cause);
  }

  public VposSdkException(String message) {
    super(message);
  }
}
