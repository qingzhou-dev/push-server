package dev.qingzhou.pushserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push.portal.wecom")
public class PortalWecomProperties {

    private String baseUrl = "https://qyapi.weixin.qq.com";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
