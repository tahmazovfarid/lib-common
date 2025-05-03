package az.ailab.lib.common.filter;

import az.ailab.lib.common.constant.ExtendedHttpHeaders;
import az.ailab.lib.common.constant.MdcConstants;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Slf4j
@RequiredArgsConstructor
public class TraceFilter implements Filter {

    private static final Map<String, String> REQUEST_HEADERS_TO_MDC = Map.of(
            ExtendedHttpHeaders.X_REAL_IP_HEADER, MdcConstants.CLIENT_IP,
            ExtendedHttpHeaders.X_USER_ID_HEADER, MdcConstants.USER_ID
    );

    private final Tracer tracer;

    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("TraceFilter initialized with Tracer: {}", tracer);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest) ||
                !(response instanceof HttpServletResponse httpResponse)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            processRequestHeaders(httpRequest);
            logTraceInfoIfDebugEnabled(httpRequest);
            addTraceHeaders(httpResponse);

            filterChain.doFilter(request, response);
        } finally {
            // Clean up only the MDC entries we created
            clearMdc();
        }
    }

    private void processRequestHeaders(HttpServletRequest request) {
        REQUEST_HEADERS_TO_MDC.forEach((headerName, mdcKey) -> {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                MDC.put(mdcKey, headerValue);
            }
        });
    }

    private void addTraceHeaders(HttpServletResponse response) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            String spanId = currentSpan.context().spanId();

            if (traceId != null && !traceId.isEmpty()) {
                response.setHeader(ExtendedHttpHeaders.X_TRACE_ID_HEADER, traceId);
                log.debug("Added trace ID to response: {}", traceId);
            } else {
                log.warn("No trace ID available from current span");
            }

            if (spanId != null && !spanId.isEmpty()) {
                response.setHeader(ExtendedHttpHeaders.X_SPAN_ID_HEADER, spanId);
                log.debug("Added span ID to response: {}", spanId);
            } else {
                log.warn("No span ID available from current span");
            }
        } else {
            log.warn("No active span found when adding trace headers to response");
        }
    }

    private void clearMdc() {
        REQUEST_HEADERS_TO_MDC.values().forEach(MDC::remove);
    }

    private void logTraceInfoIfDebugEnabled(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                log.debug("Trace context: [{}] [{}] {} {}",
                        currentSpan.context().traceId(),
                        currentSpan.context().spanId(),
                        request.getMethod(),
                        request.getRequestURI());
            } else {
                log.debug("No active span for: {} {}",
                        request.getMethod(),
                        request.getRequestURI());
            }
        }
    }

}