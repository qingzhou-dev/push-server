package dev.qingzhou.pushserver.v2.web;

import dev.qingzhou.pushserver.v2.model.V2User;
import dev.qingzhou.pushserver.v2.service.V2UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/me")
public class V2MeController {

    private final V2UserService userService;

    public V2MeController(V2UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public V2Response<V2UserResponse> me(HttpSession session) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2User user = userService.getById(userId);
        if (user == null) {
            throw new V2Exception(V2Status.NOT_FOUND, "User not found");
        }
        return V2Response.ok(toResponse(user));
    }

    @PutMapping("/password")
    public V2Response<Void> updatePassword(
            @Valid @RequestBody V2PasswordUpdateRequest request,
            HttpSession session
    ) {
        Long userId = V2SessionSupport.requireUserId(session);
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return V2Response.ok("password updated", null);
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
