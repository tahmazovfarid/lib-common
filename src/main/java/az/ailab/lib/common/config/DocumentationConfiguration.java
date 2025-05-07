package az.ailab.lib.common.config;

import az.ailab.lib.common.config.properties.SwaggerProperties;
import az.ailab.lib.common.model.vo.DocumentSample;
import az.ailab.lib.common.util.ResourceUtil;
import az.ailab.lib.common.util.SwaggerUtil;
import io.swagger.v3.oas.models.examples.Example;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration for Swagger documentation.
 * <p>
 * This configuration automatically enables Swagger documentation
 * if the property `swagger.enabled=true` is set in application configuration.
 * It provides request and response examples for API endpoints using the
 * {@link OpenApiCustomizer} feature.
 * </p>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * swagger:
 *   documentSamples:
 *     - endpoint: "/api/v1/user"
 *       documentMap:
 *         request: "classpath:/samples/user_request.json"
 *         requestDescription: "User request example"
 *         successResponse: "classpath:/samples/user_success.json"
 *         successResponseDescription: "Successful user response"
 *         failResponse: "classpath:/samples/user_fail.json"
 *         failResponseDescription: "Failed user response"
 * </pre>
 *
 * <h2>Usage:</h2>
 * - Simply add this library as a dependency.
 * - Configure the properties as shown above.
 * - Swagger will automatically include example requests and responses for API endpoints.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(OpenApiCustomizer.class)
public class DocumentationConfiguration {

    private static final String OK = "200";
    private static final String BAD_REQUEST = "400";

    private final SwaggerProperties swaggerProperties;

    /**
     * Registers a custom OpenAPI modifier that adds example request and response bodies
     * to the generated API documentation.
     *
     * @return an OpenApiCustomizer that enhances the OpenAPI documentation.
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        List<DocumentSample> documentSamples = swaggerProperties.getDocumentSamples();
        return openApi -> documentSamples.forEach(document -> {
            SwaggerUtil.getRequestBodyContent(openApi, document.getEndpoint())
                    .ifPresent(content -> document.getDocumentMap()
                            .forEach((key, value) -> {
                                Example example = new Example();
                                example.setDescription(ResourceUtil.readResourceFile(value.getRequestDescription()));
                                example.setValue(ResourceUtil.readResourceFile(value.getRequest()));
                                content.addExamples(key, example);
                            }));

            SwaggerUtil.getResponseBodyContent(openApi, document.getEndpoint(), OK)
                    .ifPresent(content -> document.getDocumentMap()
                            .forEach((key, value) -> {
                                Example example = new Example();
                                example.setDescription(ResourceUtil.readResourceFile(value.getSuccessResponseDescription()));
                                example.setValue(ResourceUtil.readResourceFile(value.getSuccessResponse()));
                                content.addExamples(key, example);
                            }));

            SwaggerUtil.getResponseBodyContent(openApi, document.getEndpoint(), BAD_REQUEST)
                    .ifPresent(content -> document.getDocumentMap()
                            .forEach((key, value) -> {
                                Example example = new Example();
                                example.setDescription(ResourceUtil.readResourceFile(value.getFailResponseDescription()));
                                example.setValue(ResourceUtil.readResourceFile(value.getFailResponse()));
                                content.addExamples(key, example);
                            }));
        });
    }

}