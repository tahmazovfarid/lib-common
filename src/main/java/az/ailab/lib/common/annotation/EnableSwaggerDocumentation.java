package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.config.DocumentationConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Import;

/**
 * Auto configuration for Swagger documentation.
 * <p>
 * This configuration automatically enables Swagger documentation
 * if the property `swagger.enabled=true` is set in application configuration.
 * It provides request and response examples for API endpoints using the
 * {@link OpenApiCustomizer} feature.
 * </p>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * swagger:
 *   documentSamples:
 *     - endpoint: "/api/v1/user"
 *       documentMap:
 *         request: "classpath:/samples/user_request.json"
 *         requestDescription: "User request example"
 *         successResponse: "classpath:/samples/user_success.json"
 *         successResponseDescription: "Successful user response"
 *         failResponse: "classpath:/samples/user_fail.json"
 *         failResponseDescription: "Failed user response"
 * </pre>
 *
 * <h2>Usage:</h2>
 * - Simply add this library as a dependency.
 * - Configure the properties as shown above.
 * - Swagger will automatically include example requests and responses for API endpoints.
 *
 * @author tahmazovfarid
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(DocumentationConfiguration.class)
public @interface EnableSwaggerDocumentation {

}
