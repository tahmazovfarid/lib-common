package az.ailab.lib.common.util;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Optional;

public final class SwaggerUtil {

    private SwaggerUtil() {
    }

    public static Optional<MediaType> getRequestBodyContent(OpenAPI openAPI, String path) {
        return Optional.ofNullable(openAPI.getPaths().get(path))
                .map(PathItem::getPost)
                .map(Operation::getRequestBody)
                .map(RequestBody::getContent)
                .map(content -> content.get(APPLICATION_JSON_VALUE));
    }

    public static Optional<MediaType> getResponseBodyContent(OpenAPI openAPI,
                                                             String path,
                                                             String responseCode) {
        return Optional.ofNullable(openAPI.getPaths().get(path))
                .map(PathItem::getPost)
                .map(Operation::getResponses)
                .map(responses -> responses.get(responseCode))
                .map(ApiResponse::getContent)
                .flatMap(content -> Optional.ofNullable(content.get(APPLICATION_JSON_VALUE)));
    }

}
