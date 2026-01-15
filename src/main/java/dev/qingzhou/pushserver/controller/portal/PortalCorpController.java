package dev.qingzhou.pushserver.controller.portal;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalCorpConfigRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;
import dev.qingzhou.pushserver.model.vo.portal.PortalCorpResponse;
import dev.qingzhou.pushserver.service.PortalCorpConfigService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/corp")
public class PortalCorpController {

    private final PortalCorpConfigService corpConfigService;

    public PortalCorpController(PortalCorpConfigService corpConfigService) {
        this.corpConfigService = corpConfigService;
    }

    @GetMapping
    public PortalResponse<PortalCorpResponse> getCorp(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalCorpConfig config = corpConfigService.getByUserId(userId);
        PortalCorpResponse response = new PortalCorpResponse();
        if (config != null) {
            response.setCorpId(config.getCorpId());
        }
        return PortalResponse.ok(response);
    }

    @PutMapping
    public PortalResponse<PortalCorpResponse> upsert(
            @Valid @RequestBody PortalCorpConfigRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalCorpConfig config = corpConfigService.upsert(userId, request.getCorpId());
        PortalCorpResponse response = new PortalCorpResponse();
        response.setCorpId(config.getCorpId());
        return PortalResponse.ok(response);
    }
}
