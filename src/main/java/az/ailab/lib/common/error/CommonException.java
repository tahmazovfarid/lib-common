package az.ailab.lib.common.error;

import az.ailab.lib.common.model.dto.response.ErrorResponse;
import az.ailab.lib.common.model.dto.response.ResponseWrapper;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {

    private Integer status;
    private String path;
    private String method;
    private String message;
    private LocalDateTime timestamp;

    public CommonException(Integer status, String path, String method, String message, LocalDateTime timestamp) {
        Objects.requireNonNull(status, "HttpStatus must be defined!");
        this.status = status;
        this.path = path;
        this.method = method;
        this.message = message;
        this.timestamp = timestamp;
    }

    public CommonException(ResponseWrapper<ErrorResponse> errorResponseWrapper) {
        if (Objects.nonNull(errorResponseWrapper)) {
            ErrorResponse errorResponse = errorResponseWrapper.getError();

            if (Objects.nonNull(errorResponse)) {
                this.status = errorResponse.getStatus();
                this.path = errorResponse.getPath();
                this.method = errorResponse.getMethod();
                this.message = errorResponse.getMessage();
                this.timestamp = errorResponse.getTimestamp();
            }
        }
    }

}
