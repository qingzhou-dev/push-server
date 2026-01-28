package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.manager.wecom.WecomAgentInfo;
import dev.qingzhou.pushserver.manager.wecom.WecomApiClient;
import dev.qingzhou.pushserver.manager.wecom.WecomToken;
import dev.qingzhou.pushserver.mapper.portal.PortalWecomAppMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.service.PortalAccessTokenService;
import dev.qingzhou.pushserver.service.PortalCorpConfigService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalWecomAppServiceImpl extends ServiceImpl<PortalWecomAppMapper, PortalWecomApp> implements PortalWecomAppService {

    private final WecomApiClient wecomApiClient;
    private final PortalAccessTokenService accessTokenService;
    private final PortalCorpConfigService corpConfigService;

    public PortalWecomAppServiceImpl(
            WecomApiClient wecomApiClient,
            PortalAccessTokenService accessTokenService,
            PortalCorpConfigService corpConfigService
    ) {
        this.wecomApiClient = wecomApiClient;
        this.accessTokenService = accessTokenService;
        this.corpConfigService = corpConfigService;
    }

    @Override
    public PortalWecomApp addApp(Long userId, String agentId, String secret) {
        if (!StringUtils.hasText(agentId) || !StringUtils.hasText(secret)) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "AgentId 和 Secret 不能为空");
        }
        if (existsApp(userId, agentId)) {
            throw new PortalException(PortalStatus.CONFLICT, "Agent 已存在");
        }
        PortalCorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        WecomToken token = accessTokenService.fetchToken(corpConfig.getCorpId(), secret);
        WecomAgentInfo info = wecomApiClient.getAgentInfo(token.getAccessToken(), agentId);
        PortalWecomApp app = new PortalWecomApp();
        long now = System.currentTimeMillis();
        app.setUserId(userId);
        app.setAgentId(agentId.trim());
        app.setSecret(secret.trim());
        app.setName(info.getName());
        app.setAvatarUrl(info.getAvatarUrl());
        app.setDescription(info.getDescription());
        app.setCreatedAt(now);
        app.setUpdatedAt(now);
        save(app);
        return app;
    }

    @Override
    public List<PortalWecomApp> listByUser(Long userId) {
        QueryWrapper<PortalWecomApp> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("created_at");
        return list(wrapper);
    }

    @Override
    public PortalWecomApp requireByUser(Long userId, Long appId) {
        PortalWecomApp app = getById(appId);
        if (app == null || !app.getUserId().equals(userId)) {
            throw new PortalException(PortalStatus.NOT_FOUND, "应用未找到");
        }
        return app;
    }

    @Override
    public PortalWecomApp updateApp(Long userId, Long appId, String secret, String token, String encodingAesKey) {
        PortalWecomApp app = requireByUser(userId, appId);
        boolean changed = false;
        
        if (StringUtils.hasText(secret) && !secret.equals(app.getSecret())) {
            app.setSecret(secret.trim());
            // 如果 Secret 变更，需要清除 Token 缓存
            accessTokenService.evict(app.getId());
            changed = true;
        }
        
        if (token != null && !token.equals(app.getToken())) {
            app.setToken(token.trim());
            changed = true;
        }
        
        if (encodingAesKey != null && !encodingAesKey.equals(app.getEncodingAesKey())) {
            app.setEncodingAesKey(encodingAesKey.trim());
            changed = true;
        }
        
        if (changed) {
            app.setUpdatedAt(System.currentTimeMillis());
            updateById(app);
        }
        
        return app;
    }

    @Override
    public PortalWecomApp syncApp(Long userId, Long appId) {
        PortalWecomApp app = requireByUser(userId, appId);
        PortalCorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        String accessToken = accessTokenService.getToken(app.getId(), corpConfig.getCorpId(), app.getSecret());
        WecomAgentInfo info = wecomApiClient.getAgentInfo(accessToken, app.getAgentId());
        app.setName(info.getName());
        app.setAvatarUrl(info.getAvatarUrl());
        app.setDescription(info.getDescription());
        app.setUpdatedAt(System.currentTimeMillis());
        updateById(app);
        return app;
    }

    @Override
    public void deleteApp(Long userId, Long appId) {
        PortalWecomApp app = requireByUser(userId, appId);
        removeById(app.getId());
        accessTokenService.evict(app.getId());
    }

    private boolean existsApp(Long userId, String agentId) {
        QueryWrapper<PortalWecomApp> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("agent_id", agentId.trim());
        return count(wrapper) > 0;
    }
}
