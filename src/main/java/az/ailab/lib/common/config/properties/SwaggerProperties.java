package az.ailab.lib.common.config.properties;

import az.ailab.lib.common.model.vo.DocumentSample;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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
 * swagger:
 *   title: "My API"
 *   description: "API for managing users"
 *   version: "1.0.0"
 *   contactName: "Farid Tahmazov"
 *   contactEmail: "farid.tahmazov@ailab.az"
 *   documentSamples:
 *     - endpoint: "/api/v1/orders"
 *       documentMap:
 *         request: "classpath:/samples/order_request.json"
 *         successResponse: "classpath:/samples/order_success.json"
 *         failResponse: "classpath:/samples/order_fail.json"
 * </pre>
 *
 * <p>
 * The properties are automatically loaded and can be accessed via dependency injection.
 * </p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    private String title = "API";
    private String description = "API Documentation";
    private String version = "1.0.0";
    private String termsOfServiceUrl;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String license;
    private String licenseUrl;
    private List<@Valid DocumentSample> documentSamples = new ArrayList<>();

}