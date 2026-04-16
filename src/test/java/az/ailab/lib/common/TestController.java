package az.ailab.lib.common;

import az.ailab.lib.common.error.ServiceException;
import az.ailab.lib.common.model.dto.response.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple REST controller used in integration tests.
 *
 * <p>Verifies:
 * <ul>
 *   <li>Swagger/OpenAPI annotation scanning</li>
 *   <li>ResponseWrapper serialization</li>
 *   <li>Error handler behaviour</li>
 * </ul>
 */
@Tag(name = "Test", description = "Endpoints for lib-common integration verification")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "Hello endpoint", description = "Returns a simple greeting")
    @GetMapping("/hello")
    public ResponseEntity<ResponseWrapper<Map<String, String>>> hello() {
        return ResponseEntity.ok(ResponseWrapper.ok(Map.of("message", "Hello from lib-common!")));
    }

    @Operation(summary = "Ping endpoint", description = "Returns pong")
    @GetMapping("/ping")
    public ResponseEntity<ResponseWrapper<String>> ping() {
        return ResponseEntity.ok(ResponseWrapper.ok("pong"));
    }

    @Operation(summary = "Error endpoint", description = "Throws a ServiceException to test error handling")
    @GetMapping("/error")
    public ResponseEntity<Void> triggerError() {
        throw new ServiceException(HttpStatus.BAD_REQUEST, "Triggered test error");
    }

}
