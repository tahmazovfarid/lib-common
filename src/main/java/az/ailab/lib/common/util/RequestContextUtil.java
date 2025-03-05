package az.ailab.lib.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Utility class for retrieving request-related context information.
 * <p>
 * This class provides static methods to obtain the current request path
 * and HTTP method in a Spring web application. It relies on {@link ContextUtil}
 * to fetch the active {@link HttpServletRequest}.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * String requestPath = RequestContextUtil.getPath();
 * String requestMethod = RequestContextUtil.getMethod();
 * }
 * </pre>
 *
 * <h2>Behavior:</h2>
 * <ul>
 *     <li> Retrieves the current request path.</li>
 *     <li> Retrieves the current request HTTP method.</li>
 *     <li> Defaults to "/" if no request is available.</li>
 *     <li> Defaults to "GET" if no method is available.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class RequestContextUtil {

    private RequestContextUtil() {
    }

    /**
     * Retrieves the current request path.
     * <p>
     * This method fetches the servlet path of the active request.
     * If no request is available, it returns the root path ("/").
     * </p>
     *
     * @return the current request path, or "/" if no request exists
     */
    public static String getPath() {
        return ContextUtil.getServletRequest()
                .map(HttpServletRequest::getServletPath)
                .orElse("/");
    }

    /**
     * Retrieves the current HTTP method.
     * <p>
     * This method fetches the HTTP method (e.g., GET, POST) of the active request.
     * If no request is available, it defaults to "GET".
     * </p>
     *
     * @return the current request method, or "GET" if no request exists
     */
    public static String getMethod() {
        return ContextUtil.getServletRequest()
                .map(HttpServletRequest::getMethod)
                .orElse(RequestMethod.GET.name());
    }

}
