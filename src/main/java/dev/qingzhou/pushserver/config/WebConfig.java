package dev.qingzhou.pushserver.config;

import dev.qingzhou.pushserver.aspect.PortalAuthInterceptor;
import dev.qingzhou.pushserver.aspect.SecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SecurityInterceptor securityInterceptor;
    private final PortalAuthInterceptor authInterceptor;

    public WebConfig(SecurityInterceptor securityInterceptor, PortalAuthInterceptor authInterceptor) {
        this.securityInterceptor = securityInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/v1/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/portal/**")
                .excludePathPatterns(
                        "/portal/auth/login",
                        "/portal/auth/register"
                );
    }
}
