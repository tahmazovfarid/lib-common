package az.ailab.lib.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Swagger integration.
 * <p>
 * This class allows customizing Swagger settings through `application.yml` or `application.properties`.
 * It provides options to define API metadata, contact details, and request/response documentation examples.
 * </p>
 *
 * <h2>Example Configuration</h2>
 * <pre>
 * spring:
 *   jpa:
 *      sql-logging:
 *          enabled: true
 *          show-parameters: true
 * </pre>
 *
 * <p>
 * The properties are automatically loaded and can be accessed via dependency injection.
 * </p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.jpa.sql-logging")
public class SqlLoggingProperties {

    /**
     * Master switch for enabling/disabling Logging.
     * Default is false.
     */
    private boolean enabled = false;

    private boolean showParameters = false;

}