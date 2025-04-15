package az.ailab.lib.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import az.ailab.lib.common.aspect.LoggingAspect;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * A logging aspect that provides automatic method entry, exit, and exception logging
 * for services and controllers.
 * <p>This aspect logs method calls, execution time, and exceptions while allowing detailed
 * logging in specific environments like "local", "dev", "test", and "preprod".</p>
 *
 * <h2>Features:</h2>
 * - Logs method entry and exit with arguments and return values (for debugging).
 * - Logs exceptions, providing detailed stack traces in specific environments.
 * - Can be extended to customize pointcuts for different application layers.
 *
 * @author tahmazovfarid
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(LoggingAspect.class)
public @interface EnableLogging {

}
