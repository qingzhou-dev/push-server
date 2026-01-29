package dev.qingzhou.pushserver.service;

import dev.qingzhou.pushserver.manager.wecom.WecomToken;
import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;

public interface PortalAccessTokenService {

    WecomToken fetchToken(String corpId, String secret, PortalProxyConfig proxyConfig);

    String getToken(Long appId, String corpId, String secret, PortalProxyConfig proxyConfig);

    void evict(Long appId);
}
