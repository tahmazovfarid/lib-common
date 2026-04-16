package az.ailab.lib.common.config;

import az.ailab.lib.common.annotation.EnableJacksonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.util.StdDateFormat;

/**
 * Provides standardized configuration for Jackson's JsonMapper within a Spring application.
 *
 * <p>This configuration class creates a consistent JsonMapper bean that can be used
 * throughout the application for all JSON serialization and deserialization needs.
 * It ensures that all Jackson-related operations follow the same rules, especially
 * important in applications that process dates or handle complex object hierarchies.</p>
 *
 * <p>The configuration applies the following settings to the JsonMapper:</p>
 * <ul>
 *   <li>Java 8 date/time types (LocalDate, LocalDateTime, etc.) are supported natively
 *       in Jackson 3 without additional module registration</li>
 *   <li>Configures lenient deserialization by ignoring unknown JSON properties,
 *       making the application more robust when handling external APIs</li>
 *   <li>Dates are serialized in ISO-8601 format by default in Jackson 3</li>
 *   <li>Sets a standardized date format with properly formatted time zones that include colons
 *       (e.g., "2023-04-15T14:30:00+02:00" instead of "2023-04-15T14:30:00+0200"),
 *       ensuring compliance with ISO-8601 and improving interoperability with other systems</li>
 * </ul>
 * <p>To use this configuration, either import it directly or prefer using the
 * {@code @EnableJacksonConfiguration} annotation in your application.</p>
 *
 * @see tools.jackson.databind.json.JsonMapper
 * @see tools.jackson.databind.util.StdDateFormat
 * @see EnableJacksonConfiguration
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public JsonMapper objectMapper() {
        return JsonMapper.builder()
                .defaultDateFormat(new StdDateFormat().withColonInTimeZone(true))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
    }

}