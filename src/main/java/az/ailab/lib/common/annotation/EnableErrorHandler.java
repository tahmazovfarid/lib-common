package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.error.CommonException;
import az.ailab.lib.common.error.ServiceException;
import az.ailab.lib.common.error.handler.CommonErrorHandler;
import az.ailab.lib.common.model.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.SocketTimeoutException;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

/**
 * Enables Global exception handler for standardizing API error responses.
 * <p>
 * This annotation imports {@link CommonErrorHandler} which provides a comprehensive
 * exception handling mechanism that intercepts and standardizes exceptions thrown
 * in the application, ensuring all errors are returned in a consistent format using
 * {@link ErrorResponse}.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>
 * &#64;EnableErrorHandler
 * &#64;SpringBootApplication
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * </pre>
 *
 * <h2>Handles:</h2>
 * <ul>
 *   <li><strong>Service Exceptions</strong>:
 *   Through {@link az.ailab.lib.common.error.ServiceException} with customizable status, code, and message</li>
 *   <li><strong>Common Exceptions</strong>: Using {@link CommonException} for generic application errors</li>
 *   <li><strong>Validation Errors</strong>: Handles {@link ConstraintViolationException}, {@link MethodArgumentNotValidException}
 *       with detailed field validation messages</li>
 *   <li><strong>Network Errors</strong>: Manages {@link java.net.ConnectException}, {@link SocketTimeoutException}
 *       with appropriate timeout responses</li>
 *   <li><strong>Request Parameter Errors</strong>: Catches {@link MissingServletRequestParameterException},
 *       {@link MissingPathVariableException}</li>
 *   <li><strong>Internal Server Errors</strong>: Provides fallback handling for all unhandled exceptions</li>
 * </ul>
 *
 * <p>All error responses provide standardized structure containing:</p>
 * <ul>
 *   <li>Error code</li>
 *   <li>HTTP status</li>
 *   <li>Request method and path</li>
 *   <li>Human-readable message</li>
 *   <li>Timestamp</li>
 *   <li>Detailed validation errors (when applicable)</li>
 *   <li>Additional context details (when provided)</li>
 * </ul>
 *
 * @see CommonErrorHandler
 * @see ErrorResponse
 * @see ServiceException
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 *
 * @author tahmazovfarid
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(CommonErrorHandler.class)
public @interface EnableErrorHandler {

}
