package az.ailab.lib.common.model.dto.response;

import az.ailab.lib.common.error.ServiceException;
import az.ailab.lib.common.util.RequestContextUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {

    private String code;
    private Integer status;
    private String method;
    private String path;
    private String message;
    private LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ValidationError> errors;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> details;

    public static ErrorResponse fromServiceException(@NotNull ServiceException ex) {
        return ErrorResponse.builder().code(ex.getCodeAsStr()).status(ex.getStatus()).method(ex.getMethod()).path(ex.getPath())
                .message(ex.getMessage()).timestamp(ex.getTimestamp()).errors(ex.getErrors()).details(ex.getDetails()).build();
    }

    public static ErrorResponse build(@NotNull HttpStatus status, @NotNull String message) {
        return build(status, message, Collections.emptyList(), Collections.emptyMap());
    }

    public static ErrorResponse build(@NotNull HttpStatus status, @NotNull String message, List<ValidationError> errors) {

        return build(status, message, errors, Collections.emptyMap());
    }

    public static ErrorResponse build(@NotNull HttpStatus status, @NotNull String message, List<ValidationError> errors,
                                      Map<String, Object> details) {

        return builder().code(status.name().toLowerCase()).status(status.value()).path(RequestContextUtil.getPath())
                .method(RequestContextUtil.getMethod()).message(message).timestamp(LocalDateTime.now())
                .errors(Optional.ofNullable(errors).orElse(Collections.emptyList()))
                .details(Optional.ofNullable(details).orElse(Collections.emptyMap())).build();
    }

}