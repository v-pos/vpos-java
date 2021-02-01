package ao.vpos.vpos.model;

public interface Response<T> {
    Integer getStatusCode();
    String getMessage();
    T getData();
    String getLocation();
}
