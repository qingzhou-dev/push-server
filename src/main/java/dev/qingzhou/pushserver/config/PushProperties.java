package dev.qingzhou.pushserver.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "push")
public class PushProperties {

    private final Auth auth = new Auth();
    private final Wecom wecom = new Wecom();
    private final Security security = new Security();

    public Auth getAuth() {
        return auth;
    }

    public Wecom getWecom() {
        return wecom;
    }

    public Security getSecurity() {
        return security;
    }

    public static class Auth {
        @NotBlank
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Wecom {
        @NotBlank
        private String appKey;
        @NotBlank
        private String appSecret;
        @NotBlank
        private String agentId;
        private String webhookUrl;

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getWebhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }
    }

    public static class Security {
        public static final int DEFAULT_BLOCK_MINUTES = 30;
        public static final int DEFAULT_FAIL_WINDOW_MINUTES = 5;
        public static final int DEFAULT_MAX_FAILS = 5;
        public static final long DEFAULT_RATE_LIMIT_CAPACITY = 10;
        public static final long DEFAULT_RATE_LIMIT_QPS = 1;

        private Integer blockMinutes;
        private Integer failWindowMinutes;
        private Integer maxFails;
        private Long rateLimitCapacity;
        private Long rateLimitQps;

        public int getBlockMinutes() {
            return blockMinutes != null ? blockMinutes : DEFAULT_BLOCK_MINUTES;
        }

        public void setBlockMinutes(Integer blockMinutes) {
            this.blockMinutes = blockMinutes;
        }

        public int getFailWindowMinutes() {
            return failWindowMinutes != null ? failWindowMinutes : DEFAULT_FAIL_WINDOW_MINUTES;
        }

        public void setFailWindowMinutes(Integer failWindowMinutes) {
            this.failWindowMinutes = failWindowMinutes;
        }

        public int getMaxFails() {
            return maxFails != null ? maxFails : DEFAULT_MAX_FAILS;
        }

        public void setMaxFails(Integer maxFails) {
            this.maxFails = maxFails;
        }

        public long getRateLimitCapacity() {
            return rateLimitCapacity != null ? rateLimitCapacity : DEFAULT_RATE_LIMIT_CAPACITY;
        }

        public void setRateLimitCapacity(Long rateLimitCapacity) {
            this.rateLimitCapacity = rateLimitCapacity;
        }

        public long getRateLimitQps() {
            return rateLimitQps != null ? rateLimitQps : DEFAULT_RATE_LIMIT_QPS;
        }

        public void setRateLimitQps(Long rateLimitQps) {
            this.rateLimitQps = rateLimitQps;
        }
    }
}
