package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.model.dto.portal.PortalInitRequest;
import dev.qingzhou.pushserver.service.PortalUserService;
import dev.qingzhou.pushserver.service.SystemConfigService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PortalInitController {

    private final PortalUserService userService;
    private final SystemConfigService configService;

    public PortalInitController(PortalUserService userService, SystemConfigService configService) {
        this.userService = userService;
        this.configService = configService;
    }

    @GetMapping("/init-status")
    public ResponseEntity<Map<String, Boolean>> getInitStatus() {
        boolean initialized = userService.count() > 0;
        return ResponseEntity.ok(Map.of("initialized", initialized));
    }

    @PostMapping("/init")
    public ResponseEntity<Map<String, String>> initialize(@Valid @RequestBody PortalInitRequest request) {
        if (userService.count() > 0) {
            return ResponseEntity.badRequest().body(Map.of("msg", "System already initialized"));
        }

        // Create Admin User
        userService.register(request.getUsername(), request.getPassword());

        // Save Turnstile Config
        configService.setTurnstileConfig(
                request.isTurnstileEnabled(),
                request.getTurnstileSiteKey(),
                request.getTurnstileSecretKey()
        );

        return ResponseEntity.ok(Map.of("msg", "Initialization successful"));
    }
}
