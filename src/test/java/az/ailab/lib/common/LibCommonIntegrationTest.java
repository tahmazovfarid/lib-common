package az.ailab.lib.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests verifying that lib-common auto-configurations, REST endpoints,
 * Swagger docs, and the global error handler all wire up correctly.
 */
@SpringBootTest(classes = {TestApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class LibCommonIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("GET /api/test/hello returns 200 with ResponseWrapper payload")
    void helloEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/api/test/hello").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.message").value("Hello from lib-common!"));
    }

    @Test
    @DisplayName("GET /api/test/ping returns 200 with pong")
    void pingEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/api/test/ping").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").value("pong"));
    }

    @Test
    @DisplayName("GET /api/test/error is handled by CommonErrorHandler and returns 400")
    void errorEndpoint_isHandledByCommonErrorHandler() throws Exception {
        mockMvc.perform(get("/api/test/error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /v3/api-docs returns OpenAPI JSON (Swagger auto-config works)")
    void swaggerApiDocs_available() throws Exception {
        mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("lib-common Test API"))
                .andExpect(jsonPath("$.paths['/api/test/hello']").exists())
                .andExpect(jsonPath("$.paths['/api/test/ping']").exists());
    }

}
