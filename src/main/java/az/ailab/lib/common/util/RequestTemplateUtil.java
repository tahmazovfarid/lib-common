package az.ailab.lib.common.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for constructing HTTP request components such as
 * Basic Authorization headers and URL-encoded form bodies.
 * <p>This class is final and contains only static methods; it cannot be instantiated.</p>
 *
 * @author tahmazovfarid
 */
public final class RequestTemplateUtil {

    private static final String BASIC_PREFIX = "Basic ";
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * Private constructor to prevent instantiation.
     */
    private RequestTemplateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Builds a Basic Authorization header value by encoding the given username and password.
     * <p>
     * Example: {@code Authorization: Basic dXNlcjpwYXNzd29yZA==}
     * </p>
     *
     * @param username the client ID or username; must not be null
     * @param password the client secret or password; must not be null
     * @return the formatted Basic Authorization header value
     * @throws NullPointerException if username or password is null
     */
    private String buildBasicAuthorization(String username, String password) {
        if (username == null || password == null) {
            throw new NullPointerException("Username and password must not be null");
        }

        String credentials = username + ':' + password;
        String encoded = ENCODER.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return BASIC_PREFIX + encoded;
    }

    private String buildFormUrlEncoded(Map<String, String> params) {
        if (params == null) {
            throw new NullPointerException("Params map must not be null");
        }
        return params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    /**
     * Builds a URL-encoded form body for a single name-value pair.
     * <p>
     * Applies URL encoding to both key and value.
     * </p>
     *
     * @param key   the form parameter name; must not be null
     * @param value the form parameter value; must not be null
     * @return a string in application/x-www-form-urlencoded format
     * @throws NullPointerException if key or value is null
     */
    private String buildFormUrlEncoded(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and value must not be null");
        }
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
