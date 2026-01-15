package dev.qingzhou.pushserver.v2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.v2.mapper.V2WecomAppMapper;
import dev.qingzhou.pushserver.v2.model.V2CorpConfig;
import dev.qingzhou.pushserver.v2.model.V2WecomApp;
import dev.qingzhou.pushserver.v2.wecom.V2WecomAgentInfo;
import dev.qingzhou.pushserver.v2.wecom.V2WecomToken;
import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2Status;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class V2WecomAppService extends ServiceImpl<V2WecomAppMapper, V2WecomApp> {

    private final V2WecomApiClient wecomApiClient;
    private final V2AccessTokenService accessTokenService;
    private final V2CorpConfigService corpConfigService;

    public V2WecomAppService(
            V2WecomApiClient wecomApiClient,
            V2AccessTokenService accessTokenService,
            V2CorpConfigService corpConfigService
    ) {
        this.wecomApiClient = wecomApiClient;
        this.accessTokenService = accessTokenService;
        this.corpConfigService = corpConfigService;
    }

    public V2WecomApp addApp(Long userId, String agentId, String secret) {
        if (!StringUtils.hasText(agentId) || !StringUtils.hasText(secret)) {
            throw new V2Exception(V2Status.BAD_REQUEST, "AgentId and secret are required");
        }
        if (existsApp(userId, agentId)) {
            throw new V2Exception(V2Status.CONFLICT, "Agent already exists");
        }
        V2CorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        V2WecomToken token = accessTokenService.fetchToken(corpConfig.getCorpId(), secret);
        V2WecomAgentInfo info = wecomApiClient.getAgentInfo(token.getAccessToken(), agentId);
        V2WecomApp app = new V2WecomApp();
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

    public List<V2WecomApp> listByUser(Long userId) {
        return lambdaQuery()
                .eq(V2WecomApp::getUserId, userId)
                .orderByDesc(V2WecomApp::getCreatedAt)
                .list();
    }

    public V2WecomApp requireByUser(Long userId, Long appId) {
        V2WecomApp app = getById(appId);
        if (app == null || !app.getUserId().equals(userId)) {
            throw new V2Exception(V2Status.NOT_FOUND, "App not found");
        }
        return app;
    }

    public V2WecomApp syncApp(Long userId, Long appId) {
        V2WecomApp app = requireByUser(userId, appId);
        V2CorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        String accessToken = accessTokenService.getToken(app.getId(), corpConfig.getCorpId(), app.getSecret());
        V2WecomAgentInfo info = wecomApiClient.getAgentInfo(accessToken, app.getAgentId());
        app.setName(info.getName());
        app.setAvatarUrl(info.getAvatarUrl());
        app.setDescription(info.getDescription());
        app.setUpdatedAt(System.currentTimeMillis());
        updateById(app);
        return app;
    }

    public void deleteApp(Long userId, Long appId) {
        V2WecomApp app = requireByUser(userId, appId);
        removeById(app.getId());
        accessTokenService.evict(app.getId());
    }

    private boolean existsApp(Long userId, String agentId) {
        return lambdaQuery()
                .select(V2WecomApp::getId)
                .eq(V2WecomApp::getUserId, userId)
                .eq(V2WecomApp::getAgentId, agentId.trim())
                .one() != null;
    }
}
