package az.ailab.lib.common.config;

import az.ailab.lib.common.aspect.LoggingAspect;
import az.ailab.lib.common.config.properties.SwaggerProperties;
import az.ailab.lib.common.error.CommonErrorHandler;
import az.ailab.lib.common.filter.TraceResponseFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@AutoConfiguration
@EnableConfigurationProperties(SwaggerProperties.class)
@Import({
        SwaggerAutoConfiguration.class,
        DocumentationAutoConfiguration.class
})
public class ApplicationAutoConfiguration {

    @Bean
    public TraceResponseFilter traceResponseFilter() {
        return new TraceResponseFilter();
    }

    @Bean
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }

    @Bean
    public CommonErrorHandler commonErrorHandler() {
        return new CommonErrorHandler();
    }

}
