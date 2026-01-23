package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalAppApiKeyUpdateRequest;
import dev.qingzhou.pushserver.model.dto.portal.PortalAppCreateRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalAppApiKey;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.model.vo.portal.PortalAppApiKeyResponse;
import dev.qingzhou.pushserver.model.vo.portal.PortalAppResponse;
import dev.qingzhou.pushserver.service.PortalAppApiKeyService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/apps")
public class PortalAppController {

    private final PortalWecomAppService appService;
    private final PortalAppApiKeyService apiKeyService;

    public PortalAppController(PortalWecomAppService appService, PortalAppApiKeyService apiKeyService) {
        this.appService = appService;
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    public PortalResponse<PortalAppResponse> create(
            @Valid @RequestBody PortalAppCreateRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalWecomApp app = appService.addApp(userId, request.getAgentId(), request.getSecret());
        return PortalResponse.ok(toResponse(app));
    }

    @GetMapping
    public PortalResponse<List<PortalAppResponse>> list(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        List<PortalAppResponse> apps = appService.listByUser(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PortalResponse.ok(apps);
    }

    @DeleteMapping("/{appId}")
    public PortalResponse<Void> delete(@PathVariable Long appId, HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        appService.deleteApp(userId, appId);
        apiKeyService.removeByAppId(appId);
        return PortalResponse.ok("已删除", null);
    }

    @PostMapping("/{appId}/sync")
    public PortalResponse<PortalAppResponse> sync(@PathVariable Long appId, HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalWecomApp app = appService.syncApp(userId, appId);
        return PortalResponse.ok(toResponse(app));
    }

    @GetMapping("/{appId}/api-key")
    public PortalResponse<PortalAppApiKeyResponse> getApiKey(
            @PathVariable Long appId,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalAppApiKey record = apiKeyService.findByAppId(userId, appId);
        return PortalResponse.ok(toApiKeyResponse(appId, record));
    }

    @PostMapping("/{appId}/api-key/reset")
    public PortalResponse<PortalAppApiKeyResponse> resetApiKey(
            @PathVariable Long appId,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalAppApiKey record = apiKeyService.rotateKey(userId, appId);
        return PortalResponse.ok(toApiKeyResponse(appId, record));
    }

    @PostMapping("/{appId}/api-key")
    public PortalResponse<PortalAppApiKeyResponse> createApiKey(
            @PathVariable Long appId,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalAppApiKey record = apiKeyService.rotateKey(userId, appId);
        return PortalResponse.ok(toApiKeyResponse(appId, record));
    }

    @PutMapping("/{appId}/api-key")
    public PortalResponse<PortalAppApiKeyResponse> updateApiKey(
            @PathVariable Long appId,
            @Valid @RequestBody PortalAppApiKeyUpdateRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalAppApiKey record = apiKeyService.updateRateLimit(userId, appId, request.getRateLimitPerMinute());
        return PortalResponse.ok(toApiKeyResponse(appId, record));
    }

    @DeleteMapping("/{appId}/api-key")
    public PortalResponse<Void> deleteApiKey(
            @PathVariable Long appId,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        appService.requireByUser(userId, appId);
        apiKeyService.removeByAppId(appId);
        return PortalResponse.ok("已删除", null);
    }

    private PortalAppResponse toResponse(PortalWecomApp app) {
        PortalAppResponse response = new PortalAppResponse();
        response.setId(app.getId());
        response.setAgentId(app.getAgentId());
        response.setName(app.getName());
        response.setAvatarUrl(app.getAvatarUrl());
        response.setDescription(app.getDescription());
        response.setCreatedAt(app.getCreatedAt());
        response.setUpdatedAt(app.getUpdatedAt());
        return response;
    }

    private PortalAppApiKeyResponse toApiKeyResponse(Long appId, PortalAppApiKey record) {
        PortalAppApiKeyResponse response = new PortalAppApiKeyResponse();
        response.setAppId(appId);
        response.setHasKey(record != null);
        if (record != null) {
            response.setApiKey(record.getApiKeyPlain());
            response.setRateLimitPerMinute(record.getRateLimitPerMinute());
            response.setCreatedAt(record.getCreatedAt());
            response.setUpdatedAt(record.getUpdatedAt());
        }
        return response;
    }
}
