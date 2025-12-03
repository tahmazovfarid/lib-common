package az.ailab.lib.common.config;

import az.ailab.lib.common.resolver.PaginationRequestArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final Validator validator;

    @Bean
    public PaginationRequestArgumentResolver paginationRequestArgumentResolver() {
        return new PaginationRequestArgumentResolver(validator);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(paginationRequestArgumentResolver());
    }

}