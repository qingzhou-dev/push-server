package dev.qingzhou.pushserver.controller.portal;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalAppCreateRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.model.vo.portal.PortalAppResponse;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/apps")
public class PortalAppController {

    private final PortalWecomAppService appService;

    public PortalAppController(PortalWecomAppService appService) {
        this.appService = appService;
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
        return PortalResponse.ok("deleted", null);
    }

    @PostMapping("/{appId}/sync")
    public PortalResponse<PortalAppResponse> sync(@PathVariable Long appId, HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalWecomApp app = appService.syncApp(userId, appId);
        return PortalResponse.ok(toResponse(app));
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
}
