package az.ailab.lib.common.config;

import az.ailab.lib.common.listener.QueryExecutionListenerImpl;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configures a ProxyDataSource to intercept and log each SQL execution,
 * including elapsed time, batch size, and row/column counts.
 * <p>
 * This configuration is only loaded when SQL logging is enabled via
 * 'spring.jpa.sql.logging.enabled=true'.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "spring.jpa.sql-logging",
        name = "enabled",
        havingValue = "true"
)
public class DataSourceProxyConfig {

    private final Environment environment;

    /**
     * Wraps the primary DataSource with a ProxyDataSourceBuilder that attaches
     * our QueryExecutionListenerImpl for SQL metrics and optional parameter inlining.
     *
     * @param properties base DataSource properties
     * @return proxied DataSource for enhanced SQL logging
     */
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        DataSource delegate = properties.initializeDataSourceBuilder().build();
        log.info("Initializing ProxyDataSource for SQL tracing (profiles: {})",
                environment.getProperty("spring.profiles.active", ""));
        return ProxyDataSourceBuilder
                .create(delegate)
                .listener(new QueryExecutionListenerImpl(environment))
                .build();
    }

}
