package dev.qingzhou.pushserver.v2.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(V2WecomProperties.class)
public class V2PropertiesConfig {
}
