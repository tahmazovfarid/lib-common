package az.ailab.lib.common.config;

import az.ailab.lib.common.config.properties.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration for Swagger (OpenAPI) documentation.
 * <p>
 * This class provides automatic configuration for Swagger, enabling API documentation
 * without requiring manual setup in each microservice.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *  <li>Autoconfigures OpenAPI documentation.</li>
 *  <li>Supports JWT authentication for secured endpoints.</li>
 *  <li>Uses configuration properties from {@link SwaggerProperties}.</li>
 *  <li>Customizable API metadata such as title, description, contact, and license information.</li>
 * </ul>
 * <h2>Example Usage:</h2>
 * <pre>
 * swagger:
 *   title: "My API"
 *   description: "Detailed API Documentation"
 *   version: "1.0.0"
 *   termsOfServiceUrl: "https://example.com/terms"
 *   contactName: "API Support"
 *   contactEmail: "support@example.com"
 *   contactUrl: "https://example.com/contact"
 *   license: "Apache 2.0"
 *   licenseUrl: "https://www.apache.org/licenses/LICENSE-2.0"
 * </pre>
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(OpenAPI.class)
public class SwaggerConfiguration {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String API_KEY_NAME = "JWT";
    private static final String AUTHORIZATION_SCHEME = "Bearer";

    private final SwaggerProperties properties;

    /**
     * Defines the OpenAPI configuration for the application.
     * This automatically generates API documentation and secures it with JWT authentication.
     *
     * @return an OpenAPI bean instance.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .schemaRequirement(AUTHORIZATION_SCHEME, securityScheme())
                .addSecurityItem(securityRequirement());
    }

    /**
     * Configures security requirements for the API.
     * Ensures that endpoints requiring authentication have the correct security scheme.
     *
     * @return a {@link SecurityRequirement} instance.
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement()
                .addList(AUTHORIZATION_SCHEME);
    }

    /**
     * Defines the JWT security scheme for API authentication.
     *
     * @return a {@link SecurityScheme} instance.
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(AUTHORIZATION_HEADER)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .bearerFormat(API_KEY_NAME);
    }

    /**
     * Builds API metadata such as title, description, version, and legal information.
     *
     * @return an {@link Info} instance containing API details.
     */
    private Info apiInfo() {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .termsOfService(properties.getTermsOfServiceUrl())
                .contact(apiContact())
                .license(apiLicence());
    }

    /**
     * Defines API license information.
     *
     * @return a {@link License} instance.
     */
    private License apiLicence() {
        return new License()
                .name(properties.getLicense())
                .url(properties.getLicenseUrl());
    }

    /**
     * Defines API contact information.
     *
     * @return a {@link Contact} instance.
     */
    private Contact apiContact() {
        return new Contact()
                .name(properties.getContactName())
                .email(properties.getContactEmail())
                .url(properties.getContactUrl());
    }

}