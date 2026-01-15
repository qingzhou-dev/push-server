package dev.qingzhou.pushserver.v2.web;

import dev.qingzhou.pushserver.v2.model.V2User;
import dev.qingzhou.pushserver.v2.service.V2UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/auth")
public class V2AuthController {

    private final V2UserService userService;

    public V2AuthController(V2UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public V2Response<V2UserResponse> register(
            @Valid @RequestBody V2RegisterRequest request,
            HttpSession session
    ) {
        V2User user = userService.register(request.getAccount(), request.getPassword());
        session.setAttribute(V2SessionKeys.USER_ID, user.getId());
        return V2Response.ok(toResponse(user));
    }

    @PostMapping("/login")
    public V2Response<V2UserResponse> login(
            @Valid @RequestBody V2LoginRequest request,
            HttpSession session
    ) {
        V2User user = userService.authenticate(request.getAccount(), request.getPassword());
        session.setAttribute(V2SessionKeys.USER_ID, user.getId());
        return V2Response.ok(toResponse(user));
    }

    @PostMapping("/logout")
    public V2Response<Void> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return V2Response.ok("logged out", null);
    }

    private V2UserResponse toResponse(V2User user) {
        V2UserResponse response = new V2UserResponse();
        response.setId(user.getId());
        response.setAccount(user.getAccount());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
