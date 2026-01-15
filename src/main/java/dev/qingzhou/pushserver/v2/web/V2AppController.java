package dev.qingzhou.pushserver.v2.web;

import dev.qingzhou.pushserver.v2.model.V2WecomApp;
import dev.qingzhou.pushserver.v2.service.V2WecomAppService;
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
@RequestMapping("/v2/apps")
public class V2AppController {

    private final V2WecomAppService appService;

    public V2AppController(V2WecomAppService appService) {
        this.appService = appService;
    }

    @PostMapping
    public V2Response<V2AppResponse> create(
            @Valid @RequestBody V2AppCreateRequest request,
            HttpSession session
    ) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2WecomApp app = appService.addApp(userId, request.getAgentId(), request.getSecret());
        return V2Response.ok(toResponse(app));
    }

    @GetMapping
    public V2Response<List<V2AppResponse>> list(HttpSession session) {
        Long userId = V2SessionSupport.requireUserId(session);
        List<V2AppResponse> apps = appService.listByUser(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return V2Response.ok(apps);
    }

    @DeleteMapping("/{appId}")
    public V2Response<Void> delete(@PathVariable Long appId, HttpSession session) {
        Long userId = V2SessionSupport.requireUserId(session);
        appService.deleteApp(userId, appId);
        return V2Response.ok("deleted", null);
    }

    @PostMapping("/{appId}/sync")
    public V2Response<V2AppResponse> sync(@PathVariable Long appId, HttpSession session) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2WecomApp app = appService.syncApp(userId, appId);
        return V2Response.ok(toResponse(app));
    }

    private V2AppResponse toResponse(V2WecomApp app) {
        V2AppResponse response = new V2AppResponse();
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
