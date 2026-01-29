package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalProxyConfigRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;
import dev.qingzhou.pushserver.model.vo.portal.PortalProxyConfigResponse;
import dev.qingzhou.pushserver.service.PortalProxyConfigService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/proxy")
public class PortalProxyController {

    private final PortalProxyConfigService proxyConfigService;

    public PortalProxyController(PortalProxyConfigService proxyConfigService) {
        this.proxyConfigService = proxyConfigService;
    }

    @GetMapping
    public PortalResponse<PortalProxyConfigResponse> getProxy(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalProxyConfig config = proxyConfigService.getByUserId(userId);
        return PortalResponse.ok(PortalProxyConfigResponse.from(config));
    }

    @PutMapping
    public PortalResponse<PortalProxyConfigResponse> upsert(
            @Valid @RequestBody PortalProxyConfigRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalProxyConfig config = proxyConfigService.upsert(userId, request);
        return PortalResponse.ok(PortalProxyConfigResponse.from(config));
    }

    @DeleteMapping
    public PortalResponse<Void> delete(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        proxyConfigService.deleteByUserId(userId);
        return PortalResponse.ok(null);
    }
}
