package ao.vpos.vpos.json;

import ao.vpos.vpos.exceptions.VposSdkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class JsonSerializer {
    public <T> T deserialize(InputStream bodyStream, Class<T> clazz) {
        ObjectMapper objectMapper = getObjectMapper();

        try {
            return objectMapper.readValue(bodyStream, clazz);
        } catch (IOException e) {
            throw new VposSdkException(e);
        }
    }

    public <T> List<T> deserializeList(InputStream bodyStream, Class<T> clazz) {
        ObjectMapper objectMapper = getObjectMapper();

        try {
            CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return Collections.unmodifiableList(objectMapper.readValue(bodyStream, collectionType));
        } catch (IOException e) {
            throw new VposSdkException(e);
        }
    }

    public String serialize(Object object) {
        ObjectMapper objectMapper = getObjectMapper();

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new VposSdkException(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }
}
