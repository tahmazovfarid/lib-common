package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.filter.TraceFilter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables distributed tracing functionality in the Spring application where it's applied.
 * <p>
 * This annotation imports the {@link TraceFilter} into the Spring context and
 * activates all the tracing features provided by this filter.
 * </p>
 *
 * <h3>Features provided:</h3>
 * <ul>
 *   <li>Automatic propagation of trace identifiers across microservice calls</li>
 *   <li>Addition of contextual information to logs for HTTP requests</li>
 *   <li>Inclusion of client IP and user identifier in log entries</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * Apply this annotation to your Spring Boot application's main configuration class:
 *
 * <pre>
 * {@code
 * @SpringBootApplication
 * @EnableTracing
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }
 * </pre>
 *
 * <h3>Benefits:</h3>
 * <ul>
 *   <li>Improves system observability in distributed environments</li>
 *   <li>Facilitates troubleshooting by correlating logs across multiple services</li>
 *   <li>Provides consistent header propagation without manual implementation</li>
 * </ul>
 *
 * @see TraceFilter
 *
 * @author tahmazovfarid
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(TraceFilter.class)
public @interface EnableTracing {

}
