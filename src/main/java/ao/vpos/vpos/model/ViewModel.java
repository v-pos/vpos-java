package ao.vpos.vpos.model;

public interface ViewModel<T> {
    Integer getStatusCode();
    String getMessage();
    T getData();
}
