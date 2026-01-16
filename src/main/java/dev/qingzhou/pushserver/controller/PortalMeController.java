package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.model.dto.portal.PortalPasswordUpdateRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.model.vo.portal.PortalUserResponse;
import dev.qingzhou.pushserver.service.PortalUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/me")
public class PortalMeController {

    private final PortalUserService userService;

    public PortalMeController(PortalUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PortalResponse<PortalUserResponse> me(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalUser user = userService.getById(userId);
        if (user == null) {
            throw new PortalException(PortalStatus.NOT_FOUND, "User not found");
        }
        return PortalResponse.ok(toResponse(user));
    }

    @PutMapping("/password")
    public PortalResponse<Void> updatePassword(
            @Valid @RequestBody PortalPasswordUpdateRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return PortalResponse.ok("password updated", null);
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
