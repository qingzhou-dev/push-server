package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalAppApiKey;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;

public interface PortalAppApiKeyService extends IService<PortalAppApiKey> {

    PortalAppApiKey rotateKey(Long userId, Long appId);

    PortalAppApiKey findByAppId(Long userId, Long appId);

    void removeByAppId(Long appId);

    PortalAppApiKey updateRateLimit(Long userId, Long appId, Integer rateLimitPerMinute);

    AppAuthContext requireAppByApiKey(String apiKey);

    record AppAuthContext(PortalAppApiKey apiKey, PortalWecomApp app) {
    }
}
