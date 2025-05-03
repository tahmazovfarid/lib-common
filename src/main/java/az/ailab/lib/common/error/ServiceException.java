package az.ailab.lib.common.error;

import az.ailab.lib.common.model.dto.response.ErrorCode;
import az.ailab.lib.common.model.dto.response.ErrorResponse;
import az.ailab.lib.common.model.dto.response.ResponseWrapper;
import az.ailab.lib.common.model.dto.response.ValidationError;
import az.ailab.lib.common.util.MessageUtil;
import az.ailab.lib.common.util.RequestContextUtil;
import feign.error.FeignExceptionConstructor;
import feign.error.ResponseBody;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends CommonException {

    private String code;

    @Setter
    private List<ValidationError> errors = Collections.emptyList();

    @Setter
    private Map<String, Object> details = Collections.emptyMap();

    public ServiceException(String code, Integer status, String message) {
        super(status, RequestContextUtil.getPath(), RequestContextUtil.getMethod(), message,
                LocalDateTime.now());
        this.code = code;
    }

    public ServiceException(String code, Integer status, String message, List<ValidationError> errors) {
        super(status, RequestContextUtil.getPath(), RequestContextUtil.getMethod(), message,
                LocalDateTime.now());
        this.code = code;
        this.errors = errors;
    }

    public ServiceException(HttpStatus status, String message) {
        this(status.name(), status.value(), message);
    }

    @FeignExceptionConstructor
    public ServiceException(@ResponseBody ResponseWrapper<ErrorResponse> errorResponseWrapper) {
        super(errorResponseWrapper);

        if (Objects.nonNull(errorResponseWrapper)) {
            ErrorResponse errorResponse = errorResponseWrapper.getError();

            if (Objects.nonNull(errorResponse)) {
                this.code = errorResponse.getCode();
                this.errors = errorResponse.getErrors();
                this.details = errorResponse.getDetails();
            }
        }
    }

    public static ServiceException of(ErrorCode errorCode) {
        return new ServiceException(errorCode.code(), errorCode.getStatusCode(), errorCode.getMessage());
    }

    public static ServiceException of(ErrorCode errorCode, Object... args) {
        return new ServiceException(errorCode.code(), errorCode.getStatusCode(), resolveMessage(errorCode.getMessage(), args));
    }

    public static ServiceException forbidden() {
        return new ServiceException(HttpStatus.FORBIDDEN, "Operation not permitted!");
    }

    public static ServiceException forbidden(String message) {
        return new ServiceException(HttpStatus.FORBIDDEN, message);
    }

    public static ServiceException forbidden(String message, Object... args) {
        return new ServiceException(HttpStatus.FORBIDDEN, resolveMessage(message, args));
    }

    public static ServiceException unauthorized(String message, Object... args) {
        return new ServiceException(HttpStatus.UNAUTHORIZED, resolveMessage(message, args));
    }

    public static ServiceException unauthorized(String message) {
        return new ServiceException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ServiceException unauthorized() {
        return new ServiceException(HttpStatus.UNAUTHORIZED, "Unauthorized user!");
    }

    public static ServiceException badRequest(String message, Object... args) {
        return new ServiceException(HttpStatus.BAD_REQUEST, resolveMessage(message, args));
    }

    public static ServiceException badRequest(String message) {
        return new ServiceException(HttpStatus.BAD_REQUEST, message);
    }

    public void addError(String property, String message) {
        addError(new ValidationError(property, message));
    }

    public void addError(ValidationError error) {
        if (errors.isEmpty()) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public void addDetail(String key, Object value) {
        if (details.isEmpty()) {
            details = new HashMap<>();
        }
        details.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDetail(String name) {
        return (T) details.get(name);
    }

    public boolean is(ErrorCode errorCode) {
        return errorCode != null && errorCode.code().equals(this.code);
    }

    public String getCodeAsStr() {
        return code == null ? null : code.toLowerCase();
    }

    public String formatProperties() {
        return Optional.ofNullable(details).map(Map::keySet).orElse(Collections.emptySet()).stream().map(this::formatProperty)
                .collect(Collectors.joining(", "));
    }

    private static String resolveMessage(String message, Object... args) {
        return MessageUtil.resolveMessage(message, args);
    }

    private String formatProperty(String key) {
        return key + ": " + details.get(key);
    }

}