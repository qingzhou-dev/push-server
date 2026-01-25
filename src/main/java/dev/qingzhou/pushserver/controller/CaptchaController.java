package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.service.SystemConfigService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    private final SystemConfigService configService;

    public CaptchaController(SystemConfigService configService) {
        this.configService = configService;
    }

    public record CaptchaResponse(boolean enabled, String siteKey) {
    }

    @GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaptchaResponse> captcha() {
        return ResponseEntity.ok(new CaptchaResponse(
                configService.isTurnstileEnabled(),
                configService.getTurnstileSiteKey()
        ));
    }
}
