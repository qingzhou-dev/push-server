package dev.qingzhou.pushserver.service;

import dev.qingzhou.pushserver.manager.wecom.WecomToken;

public interface PortalAccessTokenService {

    WecomToken fetchToken(String corpId, String secret);

    String getToken(Long appId, String corpId, String secret);

    void evict(Long appId);
}
