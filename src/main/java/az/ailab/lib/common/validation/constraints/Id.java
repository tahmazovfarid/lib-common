package az.ailab.lib.common.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that an ID field is both non-null and positive.
 * This annotation combines {@link NotNull} and {@link Positive} validations
 * to enforce proper ID values in entity references.
 * Validation will return appropriate message based on the validation that fails:
 * - "{id.not_null}" if the ID is null
 * - "{id.positive}" if the ID is not positive
 *
 * @author tahmazovfarid
 */
@NotNull(message = "{id.not_null}")
@Positive(message = "{id.positive}")
@Documented
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Id {

}
