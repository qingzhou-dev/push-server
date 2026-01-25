package dev.qingzhou.pushserver.service;

public interface SystemConfigService {

    String get(String key);
    String get(String key, String defaultValue);
    void set(String key, String value);
    
    // Helper methods for Turnstile
    boolean isTurnstileEnabled();
    String getTurnstileSiteKey();
    String getTurnstileSecretKey();
    void setTurnstileConfig(boolean enabled, String siteKey, String secretKey);
}
