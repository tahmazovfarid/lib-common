package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.config.SwaggerConfiguration;
import az.ailab.lib.common.config.properties.SwaggerProperties;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Auto configuration for Swagger (OpenAPI) documentation.
 * <p>
 * This class provides automatic configuration for Swagger, enabling API documentation
 * without requiring manual setup in each microservice.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *  <li>Autoconfigures OpenAPI documentation.</li>
 *  <li>Supports JWT authentication for secured endpoints.</li>
 *  <li>Uses configuration properties from {@link SwaggerProperties}.</li>
 *  <li>Customizable API metadata such as title, description, contact, and license information.</li>
 * </ul>
 * <h2>Example Usage:</h2>
 * <pre>
 * swagger:
 *   title: "My API"
 *   description: "Detailed API Documentation"
 *   version: "1.0.0"
 *   termsOfServiceUrl: "https://example.com/terms"
 *   contactName: "API Support"
 *   contactEmail: "support@example.com"
 *   contactUrl: "https://example.com/contact"
 *   license: "Apache 2.0"
 *   licenseUrl: "https://www.apache.org/licenses/LICENSE-2.0"
 * </pre>
 *
 * @author tahmazovfarid
 */
@Target(TYPE)
@Retention(RUNTIME)
@EnableConfigurationProperties(SwaggerProperties.class)
@Import(SwaggerConfiguration.class)
public @interface EnableSwagger {

}
