package az.ailab.lib.common.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * EnvironmentPostProcessor that synchronizes the 'swagger.enabled' flag
 * to the corresponding Springdoc OpenAPI and Swagger UI properties.
 * <p>
 * Developers can simply set 'swagger.enabled' in their application configuration
 * to true or false, and this processor will automatically toggle:
 * <ul>
 *   <li>springdoc.api-docs.enabled</li>
 *   <li>springdoc.swagger-ui.enabled</li>
 * </ul>
 * </p>
 */
public class SwaggerEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(SwaggerEnvironmentPostProcessor.class);

    private static final String SWAGGER_ENABLED = "swagger.enabled";
    private static final String SPRINGDOC_API_DOCS = "springdoc.api-docs.enabled";
    private static final String SPRINGDOC_UI = "springdoc.swagger-ui.enabled";

    /**
     * Post-process the application's environment to inject springdoc properties
     * based on the 'swagger.enabled' flag.
     *
     * @param environment the current configurable environment
     * @param application the current SpringApplication
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean enabled = environment.getProperty(SWAGGER_ENABLED, Boolean.class, true);
        log.info("Swagger enabled flag is set to {}. Propagating to springdoc settings.", enabled);

        Map<String, Object> overrideProperties = Map.of(
                SPRINGDOC_API_DOCS, enabled,
                SPRINGDOC_UI, enabled
        );

        PropertySource<?> propertySource = new MapPropertySource(
                "swagger-env-post-processor", overrideProperties
        );
        environment.getPropertySources().addFirst(propertySource);
        log.info("Injected property source 'swagger-env-post-processor': {}", overrideProperties);
    }

    /**
     * Defines the order of this post-processor; highest precedence
     * ensures properties are set before most other post-processors.
     *
     * @return the order value
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
