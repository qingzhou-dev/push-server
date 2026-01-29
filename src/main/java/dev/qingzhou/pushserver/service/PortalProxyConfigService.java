package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.dto.portal.PortalProxyConfigRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;

public interface PortalProxyConfigService extends IService<PortalProxyConfig> {

    PortalProxyConfig getByUserId(Long userId);

    PortalProxyConfig upsert(Long userId, PortalProxyConfigRequest request);

    void deleteByUserId(Long userId);
}
