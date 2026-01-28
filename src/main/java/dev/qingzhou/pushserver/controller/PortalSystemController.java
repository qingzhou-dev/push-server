package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class PortalSystemController {

    private final SystemConfigService systemConfigService;

    @Value("${info.app.version:unknown}")
    private String appVersion;

    public PortalSystemController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/version")
    public PortalResponse<Map<String, String>> getVersion() {
        return PortalResponse.ok(Map.of("version", appVersion));
    }

    @GetMapping("/version/ignore")
    public PortalResponse<Map<String, String>> getIgnoreVersion() {
        String version = systemConfigService.get("ignore_version", "");
        return PortalResponse.ok(Map.of("version", version));
    }

    @PostMapping("/version/ignore")
    public PortalResponse<Void> setIgnoreVersion(@RequestBody Map<String, String> body) {
        String version = body.get("version");
        if (version != null) {
            systemConfigService.set("ignore_version", version);
        }
        return PortalResponse.ok(null);
    }
}
