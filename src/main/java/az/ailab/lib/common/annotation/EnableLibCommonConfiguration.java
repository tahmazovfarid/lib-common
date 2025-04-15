package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Provides a unified configuration mechanism by enabling all common library features.
 * <p>
 * This meta-annotation combines several configuration annotations into a single,
 * easy-to-use annotation. It simplifies application setup by importing all standard
 * configurations that are commonly needed in most applications.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>
 * &#64;EnableLibCommonConfiguration
 * &#64;SpringBootApplication
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * </pre>
 *
 * <h2>Enabled Configurations:</h2>
 * <ul>
 *   <li>{@link EnableJacksonConfiguration} - Standardized Jackson serialization settings</li>
 *   <li>{@link EnableTracing} - Request tracing for observability and debugging</li>
 *   <li>{@link EnableSwagger} - Swagger API documentation setup</li>
 *   <li>{@link EnableLogging} - Centralized logging configuration</li>
 *   <li>{@link EnableErrorHandler} - Global exception handling with standardized responses</li>
 *   <li>{@link EnableAspectJAutoProxy} - Support for aspect-oriented programming</li>
 *   <li>{@link EnableSwaggerDocumentation} - Extended Swagger documentation features</li>
 * </ul>
 *
 * <p>This annotation is designed to be applied at the application class level to
 * instantly configure all core infrastructure components, reducing boilerplate
 * and ensuring consistency across different application modules.</p>
 *
 * @author tahmazovfarid
 * @see EnableJacksonConfiguration
 * @see EnableTracing
 * @see EnableSwagger
 * @see EnableLogging
 * @see EnableErrorHandler
 * @see EnableAspectJAutoProxy
 * @see EnableSwaggerDocumentation
 */
@Target(TYPE)
@Retention(RUNTIME)
@EnableTracing
@EnableErrorHandler
@EnableLogging
@EnableSwagger
@EnableSwaggerDocumentation
@EnableJacksonConfiguration
@EnableAspectJAutoProxy
public @interface EnableLibCommonConfiguration {

}
