package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.model.dto.portal.PortalRegisterRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.model.vo.portal.PortalUserResponse;
import dev.qingzhou.pushserver.service.PortalUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/auth")
public class PortalAuthController {

    private final PortalUserService userService;

    public PortalAuthController(PortalUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public PortalResponse<PortalUserResponse> register(
            @Valid @RequestBody PortalRegisterRequest request
    ) {
        PortalUser user = userService.register(request.getAccount(), request.getPassword());
        return PortalResponse.ok(toResponse(user));
    }

    @GetMapping("/csrf")
    public PortalResponse<String> csrf(CsrfToken token) {
        return PortalResponse.ok(token.getToken());
    }

    @PostMapping("/logout")
    public PortalResponse<Void> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return PortalResponse.ok("logged out", null);
    }

    private PortalUserResponse toResponse(PortalUser user) {
        PortalUserResponse response = new PortalUserResponse();
        response.setId(user.getId());
        response.setAccount(user.getAccount());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
