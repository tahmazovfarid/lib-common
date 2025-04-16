package az.ailab.lib.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A servlet filter that provides distributed tracing and contextual logging capabilities.
 * <p>
 * This filter performs two essential functions in a microservice architecture:
 * </p>
 *
 * <h3>1. Request Context Enrichment:</h3>
 * <p>
 * Extracts HTTP headers from incoming requests and adds them to the MDC (Mapped Diagnostic Context)
 * for inclusion in log entries. Specifically, it processes:
 * </p>
 * <ul>
 *   <li><code>X-Real-IP</code> → MDC key: <code>clientIp</code> - Captures the original client IP address</li>
 *   <li><code>User-Pin</code> → MDC key: <code>userPin</code> - Tracks the authenticated user identifier</li>
 * </ul>
 *
 * <h3>2. Distributed Trace Propagation:</h3>
 * <p>
 * Adds trace and span identifiers to response headers for distributed tracing across services:
 * </p>
 * <ul>
 *   <li>MDC key: <code>traceId</code> → <code>X-Trace-Id</code> header</li>
 *   <li>MDC key: <code>spanId</code> → <code>X-Span-Id</code> header</li>
 * </ul>
 *
 * <p>
 * This filter automatically cleans up MDC entries it creates to prevent memory leaks,
 * while preserving trace identifiers managed by instrumentation systems like Micrometer.
 * </p>
 *
 * <h3>Order:</h3>
 * <p>
 * Configured with {@link Ordered#HIGHEST_PRECEDENCE} to ensure this filter executes
 * before any other filters that might require access to these MDC values.
 * </p>
 *
 * <h3>Example Log Output:</h3>
 * <pre>
 * 2023-11-20 14:32:47.123 INFO 12345 --- [   RestExecutor] c.e.PaymentController                    :
 *     [traceId=3df99d4e5e57b32b]
 *     [spanId=5e72f61c4a4d9b3f]
 *     [IP:192.168.1.42]
 *     [Pin:AB12345]
 *     Payment request processed successfully for order #12345 * </pre>
 *
 * @author tahmazovfarid
 * @see org.slf4j.MDC
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

    private static final Map<String, String> REQUEST_HEADERS_TO_MDC = Map.of(
            "X-Real-IP", "clientIp",
            "User-Pin", "userPin"
    );

    private static final Map<String, String> MDC_TO_RESPONSE_HEADERS = Map.of(
            "traceId", "X-Trace-Id",
            "spanId", "X-Span-Id"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            processRequestHeaders(request);
            addResponseTraceHeaders(response);
            filterChain.doFilter(request, response);
        } finally {
            // Clean up only the MDC entries we created
            clearMDC();
        }
    }

    private void processRequestHeaders(ServletRequest request) {
        if (request instanceof HttpServletRequest httpRequest) {
            REQUEST_HEADERS_TO_MDC.forEach((headerName, mdcKey) -> {
                String headerValue = httpRequest.getHeader(headerName);
                if (headerValue != null && !headerValue.isEmpty()) {
                    MDC.put(mdcKey, headerValue);
                }
            });
        }
    }

    private void addResponseTraceHeaders(ServletResponse response) {
        if (response instanceof HttpServletResponse httpResponse) {
            MDC_TO_RESPONSE_HEADERS.forEach((mdcKey, headerName) -> {
                String value = MDC.get(mdcKey);
                if (value != null) {
                    httpResponse.setHeader(headerName, value);
                }
            });
        }
    }

    private void clearMDC() {
        REQUEST_HEADERS_TO_MDC.values().forEach(MDC::remove);
    }

}