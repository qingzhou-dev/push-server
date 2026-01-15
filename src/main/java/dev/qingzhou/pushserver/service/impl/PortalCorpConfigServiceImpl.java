package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.mapper.portal.PortalCorpConfigMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;
import dev.qingzhou.pushserver.service.PortalCorpConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalCorpConfigServiceImpl extends ServiceImpl<PortalCorpConfigMapper, PortalCorpConfig> implements PortalCorpConfigService {

    @Override
    public PortalCorpConfig getByUserId(Long userId) {
        return lambdaQuery().eq(PortalCorpConfig::getUserId, userId).one();
    }

    @Override
    public PortalCorpConfig requireByUserId(Long userId) {
        PortalCorpConfig config = getByUserId(userId);
        if (config == null) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "Corp config is not set");
        }
        return config;
    }

    @Override
    public PortalCorpConfig upsert(Long userId, String corpId) {
        if (!StringUtils.hasText(corpId)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "CorpId is required");
        }
        PortalCorpConfig config = getByUserId(userId);
        long now = System.currentTimeMillis();
        if (config == null) {
            config = new PortalCorpConfig();
            config.setUserId(userId);
            config.setCorpId(corpId.trim());
            config.setCreatedAt(now);
            config.setUpdatedAt(now);
            save(config);
        } else {
            config.setCorpId(corpId.trim());
            config.setUpdatedAt(now);
            updateById(config);
        }
        return config;
    }
}
