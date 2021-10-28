package jsonparsing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for deserialize and serialize json
 */
public class SerializationUtil {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    /**
     * Method for converting json to pojo
     *
     * @param json   String json to convert
     * @param target the class to convert to
     * @param <T>    the class type
     * @return a single object
     * @throws IOException            if failure constructing
     * @throws ClassNotFoundException if failure finding class
     */
    public static <T> T convertJsonToPOJO(String json, Class<?> target) throws IOException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType(target));
    }

    /**
     * Method for converting json to list of pojos
     *
     * @param json   String json to convert
     * @param target the class to convert to
     * @param <T>    the class type
     * @return a list of objects
     * @throws IOException            if failure constructing
     * @throws ClassNotFoundException if failure finding class
     */
    public static <T> T convertJsonToPOJOList(String json, Class<?> target) throws IOException, ClassNotFoundException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Class.forName(target.getName())));
    }
}

