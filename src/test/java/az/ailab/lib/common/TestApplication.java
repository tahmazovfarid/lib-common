package az.ailab.lib.common;

import az.ailab.lib.common.annotation.EnableLibCommonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap application for integration tests.
 * Activates all lib-common configurations via {@link EnableLibCommonConfiguration}.
 */
@SpringBootApplication
@EnableLibCommonConfiguration
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
