package az.ailab.lib.common.error.handler;

import az.ailab.lib.common.error.ForbiddenException;
import az.ailab.lib.common.error.InternalServerException;
import az.ailab.lib.common.error.NotAuthenticatedException;
import az.ailab.lib.common.error.ServiceException;
import az.ailab.lib.common.error.ServiceUnavailableException;
import feign.error.ErrorCodes;
import feign.error.ErrorHandling;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.SocketTimeoutException;

/**
 * Annotation for handling Feign client errors by mapping HTTP response codes to custom exceptions.
 * <p>
 * This annotation is used to automatically convert Feign client response errors into structured exceptions
 * without requiring manual error handling logic. It integrates with {@code AnnotationErrorDecoder} to
 * provide a cleaner, reusable approach to error handling.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * @CommonFeignErrorHandling
 * @FeignClient(name = "user-service", url = "http://user-service/api")
 * public interface UserClient {
 *
 *     @GetMapping("/users/{id}")
 *     UserResponse getUserById(@PathVariable Long id);
 * }
 * }
 * </pre>
 *
 * <h2>Feign Configuration:</h2>
 * <pre>
 * {@code
 * @Bean
 * public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
 *     return AnnotationErrorDecoder.builderFor(UserClient.class)
 *         .withResponseBodyDecoder(new JacksonDecoder(objectMapper))
 *         .build();
 * }
 * }
 * </pre>
 *
 * <h2>Mapped HTTP Status Codes:</h2>
 * <ul>
 *     <li>400 → {@code ServiceException}</li>
 *     <li>401 → {@code NotAuthenticatedException}</li>
 *     <li>403 → {@code ForbiddenException}</li>
 *     <li>500 → {@code InternalServerException}</li>
 *     <li>503 → {@code ServiceUnavailableException}</li>
 *     <li>504 → {@code SocketTimeoutException}</li>
 * </ul>
 *
 * <h2>Benefits:</h2>
 * <ul>
 *     <li>✅ Eliminates repetitive Feign error handling code</li>
 *     <li>✅ Ensures structured and meaningful exception handling</li>
 *     <li>✅ Works with any Feign client using the same response model</li>
 * </ul>
 *
 * <p>
 * If a response status is not explicitly mapped, it defaults to {@code ServiceException}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ErrorHandling(
        codeSpecific = {
                @ErrorCodes(codes = 400, generate = ServiceException.class),
                @ErrorCodes(codes = 401, generate = NotAuthenticatedException.class),
                @ErrorCodes(codes = 403, generate = ForbiddenException.class),
                @ErrorCodes(codes = 500, generate = InternalServerException.class),
                @ErrorCodes(codes = 503, generate = ServiceUnavailableException.class),
                @ErrorCodes(codes = 504, generate = SocketTimeoutException.class)
        },
        defaultException = ServiceException.class)
public @interface CommonFeignErrorHandling {
}
