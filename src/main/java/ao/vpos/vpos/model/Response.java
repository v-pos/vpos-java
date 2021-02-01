package ao.vpos.vpos.model;

public interface BaseResponse<T> {
    Integer getStatusCode();
    String getMessage();
    T getData();
}
