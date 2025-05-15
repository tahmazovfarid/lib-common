package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.config.DataSourceProxyConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables automatic SQL logging by importing {@link DataSourceProxyConfig}.
 * <p>
 * When annotated on a Spring Boot application or configuration class, this
 * annotation activates a proxied DataSource that logs each SQL statement,
 * along with execution metrics (elapsed time, batch size, rows/columns).
 * <p>
 * Must be used in conjunction with setting:
 * <ul>
 *   <li>spring.jpa.sql.logging.enabled=true</li>
 *   <li>spring.jpa.sql.logging.show-parameters=true  (optional)</li>
 * </ul>
 * <p>
 * Example:
 * <pre class="code-java">
 * &#64;SpringBootApplication
 * &#64;EnableSqlLogging
 * public class MyApp {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApp.class, args);
 *     }
 * }
 * </pre>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(DataSourceProxyConfig.class)
public @interface EnableSqlLogging {
    // Annotation only; no fields required.
}
