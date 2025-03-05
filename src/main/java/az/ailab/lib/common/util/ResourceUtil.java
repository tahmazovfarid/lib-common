package az.ailab.lib.common.util;

import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * Utility class for reading resource files from the classpath.
 * <p>
 * This class provides a method to read the contents of a resource file
 * and return it as a string. It is useful for loading static files such
 * as configuration templates, JSON examples, and documentation snippets.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * String fileContent = ResourceUtil.readResourceFile("samples/example.json");
 * System.out.println(fileContent);
 * }
 * </pre>
 *
 * <h2>Behavior:</h2>
 * <ul>
 *     <li>Reads a file from the classpath.</li>
 *     <li>Returns file contents as a UTF-8 encoded string.</li>
 *     <li>If the file is not found or an error occurs, it returns an empty string.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class ResourceUtil {

    private ResourceUtil() {
    }

    public static String readResourceFile(String resourcePath) {
        try {
            byte[] data = new ClassPathResource(resourcePath).getInputStream().readAllBytes();
            return new String(data, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return StringUtils.EMPTY;
        }
    }

}
