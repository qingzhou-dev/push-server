package dev.qingzhou.pushserver.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        PushProperties.class,
        PortalWecomProperties.class,
        PortalDataSourceProperties.class
})
public class PushConfiguration {
}
