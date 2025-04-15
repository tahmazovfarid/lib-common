package az.ailab.lib.common.config;

import az.ailab.lib.common.annotation.EnableJacksonConfiguration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides standardized configuration for Jackson's ObjectMapper within a Spring application.
 *
 * <p>This configuration class creates a consistent ObjectMapper bean that can be used
 * throughout the application for all JSON serialization and deserialization needs.
 * It ensures that all Jackson-related operations follow the same rules, especially
 * important in applications that process dates or handle complex object hierarchies.</p>
 *
 * <p>The configuration applies the following settings to the ObjectMapper:</p>
 * <ul>
 *   <li>Adds support for Java 8 date/time types (LocalDate, LocalDateTime, etc.)
 *       through the JavaTimeModule</li>
 *   <li>Configures lenient deserialization by ignoring unknown JSON properties,
 *       making the application more robust when handling external APIs</li>
 *   <li>Ensures dates are serialized in ISO-8601 format rather than as timestamps,
 *       improving human readability and interoperability</li>
 *   <li>Sets a standardized date format with properly formatted time zones that include colons
 *       (e.g., "2023-04-15T14:30:00+02:00" instead of "2023-04-15T14:30:00+0200"),
 *       ensuring compliance with ISO-8601 and improving interoperability with other systems</li>
 * </ul>
 * <p>To use this configuration, either import it directly or prefer using the
 * {@code @EnableJacksonConfiguration} annotation in your application.</p>
 *
 * @see com.fasterxml.jackson.databind.ObjectMapper
 * @see com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
 * @see com.fasterxml.jackson.databind.util.StdDateFormat
 * @see EnableJacksonConfiguration
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Support for Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());

        // Configure deserialization features
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // This line prevents dates from being serialized as timestamps
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Include colons (e.g., "2023-04-15T14:30:00+02:00" instead of "2023-04-15T14:30:00+0200")
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        return mapper;
    }

}
