package ch.want.mylyn.youtrack;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperAccessor {

    /**
     * A Jackson {@link ObjectMapper} configured to the YouTrack API specs.
     */
    private static final ObjectMapper OBJECTMAPPER;
    static {
        OBJECTMAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private ObjectMapperAccessor() {
    }

    public static ObjectMapper objectMapper() {
        return OBJECTMAPPER;
    }
}
