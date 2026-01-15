package dev.qingzhou.pushserver.v2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push.v2.wecom")
public class V2WecomProperties {

    private String baseUrl = "https://qyapi.weixin.qq.com";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
