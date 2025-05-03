package az.ailab.lib.common.constant;

import org.springframework.http.HttpHeaders;

public class ExtendedHttpHeaders extends HttpHeaders {

    public static final String X_REAL_IP_HEADER = "X-Real-Ip";
    public static final String X_USER_ID_HEADER = "X-User-Id";
    public static final String X_TRACE_ID_HEADER = "X-Trace-Id";
    public static final String X_SPAN_ID_HEADER = "X-Span-Id";

}
