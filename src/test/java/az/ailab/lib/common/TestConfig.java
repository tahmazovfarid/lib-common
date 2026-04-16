package az.ailab.lib.common;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test-only configuration that provides stub beans required by lib-common components
 * when a full production infrastructure (e.g. Brave/Zipkin) is not available.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public Tracer tracer() {
        return Tracer.NOOP;
    }

}
