package dev.qingzhou.pushserver.v2.config;

import dev.qingzhou.pushserver.v2.interceptor.V2AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class V2WebConfig implements WebMvcConfigurer {

    private final V2AuthInterceptor authInterceptor;

    public V2WebConfig(V2AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/v2/**")
                .excludePathPatterns(
                        "/v2/auth/login",
                        "/v2/auth/register"
                );
    }
}
