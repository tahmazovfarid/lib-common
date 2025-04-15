package az.ailab.lib.common.util;

import az.ailab.lib.common.model.enums.Language;
import java.util.Optional;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for extracting and processing HTTP headers, specifically for retrieving
 * the `Accept-Language` header.
 * <p>
 * This class provides methods to fetch and process the `Accept-Language` header in different
 * formats (uppercase, lowercase) while also ensuring safe retrieval using an enumeration.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Retrieves `Accept-Language` header and converts it to uppercase or lowercase.</li>
 *     <li>Returns a default language (`AZ`) if the header is missing or invalid.</li>
 *     <li>Provides a generic method to retrieve any HTTP header from the request.</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>
 * // Retrieve the Accept-Language header in different formats
 * String lang = HeaderUtil.getAcceptLang(); // Returns "AZ" if not provided
 * String upperLang = HeaderUtil.getUpperCaseAcceptLang(); // Example: "AZ"
 * String lowerLang = HeaderUtil.getLowerCaseAcceptLang(); // Example: "az"
 *
 * // Retrieve a custom header
 * String customHeader = HeaderUtil.get("X-Custom-Header"); // Retrieves the value or null if not found
 *
 * // Retrieve a custom header safely using Optional
 * Optional&lt;String&gt; customHeaderOpt = HeaderUtil.getOpt("X-Custom-Header");
 * customHeaderOpt.ifPresent(value -> System.out.println("Header found: " + value));
 * </pre>
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class HeaderUtil {

    private HeaderUtil() {
    }

    public static String getUpperCaseAcceptLang() {
        return getAcceptLang().toUpperCase();
    }

    public static String getLowerCaseAcceptLang() {
        return getAcceptLang().toLowerCase();
    }

    /**
     * Retrieves the `Accept-Language` header from the HTTP request.
     * <p>
     * If the header is not present or contains an invalid value, it defaults to `EN`.
     * </p>
     *
     * @return the language as a string, defaulting to `"AZ"` if not found.
     */
    public static String getAcceptLang() {
        Language acceptLang = EnumUtil.getOptEnumConstant(Language.class,
                get(HttpHeaders.ACCEPT_LANGUAGE)).orElse(Language.AZ);

        return acceptLang.toString();
    }

    /**
     * Retrieves the value of a specified HTTP header.
     *
     * @param name the name of the header.
     * @return the header value as a string, or {@code null} if not found.
     */
    public static String get(String name) {
        return getOpt(name).orElse(null);
    }

    /**
     * Retrieves the value of a specified HTTP header as an {@link Optional}.
     *
     * @param name the name of the header.
     * @return an {@link Optional} containing the header value, or an empty {@link Optional} if not found.
     */
    public static Optional<String> getOpt(String name) {
        return ContextUtil.getServletRequest().map(header -> header.getHeader(name));
    }

}
