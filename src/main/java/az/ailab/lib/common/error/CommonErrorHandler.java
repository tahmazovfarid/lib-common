package az.ailab.lib.common.error;

import az.ailab.lib.common.dto.response.ResponseWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for handling common API errors.
 * <p>
 * This class intercepts and standardizes exceptions thrown in the application,
 * ensuring all errors are returned in a structured format.
 * </p>
 *
 * <h2>Handles:</h2>
 * <ul>
 *   <li>Service and common exceptions</li>
 *   <li>Validation errors (request parameters, method arguments)</li>
 *   <li>File upload size exceeded errors</li>
 *   <li>Internal server and timeout errors</li>
 *   <li>etc..</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class CommonErrorHandler extends ResponseEntityExceptionHandler {

    @Resource
    private MessageSource messageSource;

    /**
     * Handles {@link ServiceException} thrown by the application.
     * <p>
     * Logs the error details and returns a structured response with {@link ErrorResponse}.
     * </p>
     *
     * @param ex the {@link ServiceException} instance
     * @return a {@link ResponseEntity} containing a {@link ResponseWrapper} with the error details
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseWrapper<ErrorResponse>> handleServiceException(ServiceException ex) {
        log.error("Service error, status: {}, code: {}, message: {}, {}", ex.getStatus(), ex.getCode(), ex.getMessage(),
                ex.formatProperties());
        ErrorResponse error = ErrorResponse.fromServiceException(ex);

        return ResponseEntity.status(error.getStatus()).body(ResponseWrapper.error(error));
    }

    /**
     * Handles {@link CommonException}, a generic application exception.
     * <p>
     * Logs the error and returns a structured response with the status and message.
     * </p>
     *
     * @param ex the {@link CommonException} instance
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse}
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("Common exception, status: {}, message: {}", ex.getStatus(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.build(HttpStatus.valueOf(ex.getStatus()), ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    /**
     * Handles service timeouts such as {@link ConnectException} and {@link SocketTimeoutException}.
     * <p>
     * Logs the timeout error and returns an error response with HTTP status 504 (Gateway Timeout).
     * </p>
     *
     * @param ex the timeout exception
     * @return a {@link ResponseWrapper} containing the {@link ErrorResponse}
     */
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler({ConnectException.class, SocketTimeoutException.class})
    public ResponseWrapper<ErrorResponse> handleServiceTimeout(Exception ex) {
        log.error("Service timeout error, code: {}, message: {}", HttpStatus.GATEWAY_TIMEOUT, ex.getMessage());
        var response = ErrorResponse.build(HttpStatus.GATEWAY_TIMEOUT, "Service timeout error");
        return ResponseWrapper.error(response);
    }

    /**
     * Handles all unhandled exceptions.
     * <p>
     * This method catches all unexpected internal server errors and returns a
     * generic HTTP status 500 (Internal Server Error) response.
     * </p>
     *
     * @param ex the exception
     * @return a {@link ResponseWrapper} containing the {@link ErrorResponse}
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseWrapper<ErrorResponse> handleInternalServerError(Exception ex) {
        log.error("Error unexpected internal server error: code: {}, message: {}", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        var response = ErrorResponse.build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Service Error");
        return ResponseWrapper.error(response);
    }

    /**
     * Handles constraint validation exceptions for invalid request parameters.
     * <p>
     * This method logs the error and returns an HTTP status 400 (Bad Request),
     * including a detailed list of validation errors.
     * </p>
     *
     * @param ex the {@link ConstraintViolationException} instance
     * @return a {@link ResponseWrapper} containing validation error details
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseWrapper<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation error, message: {}", ex.getMessage());
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        List<ValidationError> errors = constraintViolations.stream()
                .map(constraintViolation -> new ValidationError(getField(constraintViolation),
                        getConstraintViolationMessage(constraintViolation))).collect(Collectors.toList());

        var response = ErrorResponse.build(HttpStatus.BAD_REQUEST, "Invalid argument values", errors);
        return ResponseWrapper.error(response);
    }

    private static String getField(ConstraintViolation<?> constraintViolation) {
        String propertyPath = constraintViolation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        return handleBindingException(ex, ex.getBindingResult(), headers);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status,
                                                                          WebRequest request) {
        log.error("Missing request param error message: {}", ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        var errorResponse = ErrorResponse.build(badRequest, ex.getMessage());

        return ResponseEntity.status(badRequest).headers(headers).body(ResponseWrapper.error(errorResponse));
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        log.error("Missing path variable error, message: {}", ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        var errorResponse = ErrorResponse.build(badRequest, ex.getMessage());

        return ResponseEntity.status(badRequest).headers(headers).body(ResponseWrapper.error(errorResponse));
    }

    private ResponseEntity<Object> handleBindingException(Throwable ex, BindingResult bindingResult, HttpHeaders headers) {
        log.error("Method argument not valid, message: {}", ex.getMessage());
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), getErrorBindingMessage(fieldError))).toList());

        errors.addAll(bindingResult.getGlobalErrors().stream()
                .map(globalError -> new ValidationError(
                        globalError.getObjectName(),
                        getErrorBindingMessage(globalError))).toList());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        var errorResponse = ErrorResponse.build(badRequest, "Invalid Arguments", errors);

        return ResponseEntity.status(badRequest).headers(headers).body(ResponseWrapper.error(errorResponse));
    }

    private String getErrorBindingMessage(ObjectError objectError) {
        String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
        return buildErrorMessage(message);
    }

    private String getConstraintViolationMessage(ConstraintViolation<?> constraintViolation) {
        return buildErrorMessage(constraintViolation.getMessage());
    }

    private String buildErrorMessage(String message) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(message, null, locale);
        } catch (NoSuchMessageException exception) {
            return message;
        }
    }

}