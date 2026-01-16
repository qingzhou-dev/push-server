package dev.qingzhou.pushserver.controller;

import dev.qingzhou.push.core.model.PushResult;
import dev.qingzhou.pushserver.config.PushProperties;
import dev.qingzhou.pushserver.model.dto.openapi.PushRequest;
import dev.qingzhou.pushserver.service.PushService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class PushController {

    private final PushService pushService;
    private final PushProperties properties;

    public PushController(PushService pushService, PushProperties properties) {
        this.pushService = pushService;
        this.properties = properties;
    }

    @PostMapping("/push")
    public ResponseEntity<PushResult> push(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody PushRequest request
    ) {
        if (!isAuthorized(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PushResult.fail("Unauthorized"));
        }
        try {
            PushResult result = pushService.push(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(PushResult.fail(ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PushResult.fail(ex.getMessage()));
        }
    }

    private boolean isAuthorized(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            return false;
        }
        return apiKey.equals(properties.getAuth().getKey());
    }
}
