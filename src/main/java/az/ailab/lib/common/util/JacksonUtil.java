package az.ailab.lib.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generic JSON utility methods leveraging the application's configured {@link ObjectMapper}.
 * <p>
 * Provides parsing, field extraction, object ↔ JSON conversion, and object ↔ Map conversion.
 * All operations use the Spring-managed ObjectMapper bean, allowing custom modules,
 * serializers, and configurations to apply.
 * </p>
 *
 * @author tahmazovfarid
 * @since 1.1.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JacksonUtil {

    private final ObjectMapper mapper;

    /**
     * Parses a JSON string into a {@link JsonNode}.
     *
     * @param json the JSON string; must not be null
     * @return the root node
     * @throws IllegalArgumentException if parsing fails
     */
    public JsonNode parseTree(String json) {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", json, e);
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }

    /**
     * Reads a text field from a JsonNode, throwing if missing or empty.
     *
     * @param node  the source JsonNode
     * @param field the field name
     * @return the field's text content
     * @throws IllegalArgumentException if the field is missing or empty
     */
    @Deprecated
    public String readRequiredText(JsonNode node, String field) {
        JsonNode child = node.path(field);
        if (child.isMissingNode() || child.isNull() || child.asText().isEmpty()) {
            throw new IllegalArgumentException("Required JSON field '" + field + "' is missing or empty");
        }
        return child.asText();
    }

    @Deprecated
    public String readText(JsonNode node, String field) {
        return node.path(field).asText(null);
    }

    /**
     * Converts a JSON string into an instance of the given class.
     *
     * @param json  the JSON content; must not be null
     * @param clazz the target type
     * @param <T>   the target type
     * @return the deserialized object
     * @throws IllegalArgumentException if conversion fails
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), json, e);
            throw new IllegalArgumentException("Invalid JSON for type " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Converts a Java object into a Map of properties to values.
     *
     * @param obj the source object; must not be null
     * @return a Map representation
     * @throws IllegalArgumentException if conversion fails
     */
    public Map<String, Object> toMap(Object obj) {
        try {
            return mapper.convertValue(obj, new TypeReference<>() {
            });
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert object to Map: {}", obj, e);
            throw new IllegalArgumentException("Unable to convert object to Map", e);
        }
    }

    /**
     * Extracts the set of top-level field names from a JSON object string.
     *
     * @param json the JSON content as string; must represent a JSON object
     * @return set of field names
     * @throws IllegalArgumentException if input is not a JSON object
     */
    public Set<String> getFieldNames(String json) {
        JsonNode root = parseTree(json);
        if (!root.isObject()) {
            throw new IllegalArgumentException("JSON is not an object: " + json);
        }
        Set<String> fields = new LinkedHashSet<>();
        Iterator<String> it = root.fieldNames();
        it.forEachRemaining(fields::add);
        return fields;
    }

}
