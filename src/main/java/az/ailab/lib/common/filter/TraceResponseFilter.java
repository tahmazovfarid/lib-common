package az.ailab.lib.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * A filter that adds the trace ID and span ID to HTTP response headers.
 * <p>
 * This filter extracts trace and span IDs from MDC (Mapped Diagnostic Context)
 * and includes them in the response headers. It ensures that traceability
 * is maintained across distributed services by propagating tracing information.
 * </p>
 * <p>
 * The filter is automatically applied to all incoming requests.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * After a request is processed, the response headers will include:
 * <pre>
 * X-Trace-Id: 3df99d4e5e57b32b
 * X-Span-Id: 5e72f61c4a4d9b3f
 * </pre>
 */
@Slf4j
@Order
@RequiredArgsConstructor
public class TraceResponseFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // Retrieve trace ID and span ID from MDC (set by Micrometer)
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");

        if (traceId != null) {
            httpServletResponse.setHeader(TRACE_ID_HEADER, traceId);
        }
        if (spanId != null) {
            httpServletResponse.setHeader(SPAN_ID_HEADER, spanId);
        }

        chain.doFilter(request, response);
    }

}
