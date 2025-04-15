package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.config.JacksonConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables standardized Jackson configuration within a Spring application.
 * <p>This annotation imports {@link JacksonConfiguration} class which provides
 * consistent ObjectMapper configuration throughout the application. Using this
 * annotation ensures that all JSON serialization/deserialization follows the same
 * rules, avoiding potential inconsistencies.</p>
 * <p>Typical usage:</p>
 * <pre>
 * &#64;EnableJacksonConfiguration
 * &#64;SpringBootApplication
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * </pre>
 * <p>The imported configuration provides an ObjectMapper bean with the following settings:</p>
 * <ul>
 *   <li>Java 8 date/time support via JavaTimeModule</li>
 *   <li>Lenient deserialization (ignores unknown properties)</li>
 *   <li>ISO-8601 date format instead of timestamps</li>
 *   <li>Standardized time zone formatting</li>
 * </ul>
 *
 * @author tahmazovfarid
 * @see JacksonConfiguration
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(JacksonConfiguration.class)
public @interface EnableJacksonConfiguration {

}
