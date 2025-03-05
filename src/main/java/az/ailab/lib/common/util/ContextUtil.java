package az.ailab.lib.common.util;

import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class for retrieving the current {@link HttpServletRequest} in a Spring web application.
 * <p>
 * This utility provides a safe way to access the current request using {@link RequestContextHolder}.
 * It returns an {@link Optional} to prevent {@code NullPointerException} when no request is available.
 * </p>
 * <p><strong>Note:</strong> This is a utility class and cannot be instantiated.</p>
 */
public final class ContextUtil {

    private ContextUtil() {
    }

    /**
     * Retrieves the current {@link HttpServletRequest} if available.
     * <p>
     * This method uses {@link RequestContextHolder} to fetch the request attributes and
     * extract the HTTP request safely.
     * </p>
     *
     * @return an {@link Optional} containing the {@link HttpServletRequest}, or an empty {@link Optional} if no request is present.
     */
    public static Optional<HttpServletRequest> getServletRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ContextUtil::mapServletRequestAttributes)
                .map(ContextUtil::mapHttpServletRequest);
    }

    private static ServletRequestAttributes mapServletRequestAttributes(
            RequestAttributes requestAttributes) {
        return ((ServletRequestAttributes) requestAttributes);
    }

    private static HttpServletRequest mapHttpServletRequest(
            ServletRequestAttributes servletRequestAttributes) {
        return servletRequestAttributes.getRequest();
    }

}
