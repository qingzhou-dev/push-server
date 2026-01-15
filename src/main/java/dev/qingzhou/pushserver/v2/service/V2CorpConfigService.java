package dev.qingzhou.pushserver.v2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.v2.mapper.V2CorpConfigMapper;
import dev.qingzhou.pushserver.v2.model.V2CorpConfig;
import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2Status;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class V2CorpConfigService extends ServiceImpl<V2CorpConfigMapper, V2CorpConfig> {

    public V2CorpConfig getByUserId(Long userId) {
        return lambdaQuery().eq(V2CorpConfig::getUserId, userId).one();
    }

    public V2CorpConfig requireByUserId(Long userId) {
        V2CorpConfig config = getByUserId(userId);
        if (config == null) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Corp config is not set");
        }
        return config;
    }

    public V2CorpConfig upsert(Long userId, String corpId) {
        if (!StringUtils.hasText(corpId)) {
            throw new V2Exception(V2Status.BAD_REQUEST, "CorpId is required");
        }
        V2CorpConfig config = getByUserId(userId);
        long now = System.currentTimeMillis();
        if (config == null) {
            config = new V2CorpConfig();
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
