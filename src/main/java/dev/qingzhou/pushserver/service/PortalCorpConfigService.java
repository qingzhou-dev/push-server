package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;

public interface PortalCorpConfigService extends IService<PortalCorpConfig> {

    PortalCorpConfig getByUserId(Long userId);

    PortalCorpConfig requireByUserId(Long userId);

    PortalCorpConfig upsert(Long userId, String corpId);
}
