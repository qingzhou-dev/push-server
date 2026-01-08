package dev.qingzhou.pushserver.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "push")
public class PushProperties {

    private final Auth auth = new Auth();
    private final Wecom wecom = new Wecom();

    public Auth getAuth() {
        return auth;
    }

    public Wecom getWecom() {
        return wecom;
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
}
