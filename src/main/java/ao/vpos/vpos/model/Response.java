package ao.vpos.vpos.model;

import java.io.IOException;

public interface Response<T> {
    Integer getStatusCode();
    String getMessage();
    T getData() throws IOException;
    String getLocation();
}
