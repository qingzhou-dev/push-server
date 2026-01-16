package dev.qingzhou.pushserver.config;

import dev.qingzhou.pushserver.aspect.SecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SecurityInterceptor securityInterceptor;

    public WebConfig(SecurityInterceptor securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/v1/**");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 给所有标有 @RestController 注解的类，统一添加 "/api" 前缀
        configurer.addPathPrefix("/api", c -> c.isAnnotationPresent(RestController.class));
    }
}
