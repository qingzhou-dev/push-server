package dev.qingzhou.pushserver.v2.web;

import dev.qingzhou.pushserver.v2.model.V2CorpConfig;
import dev.qingzhou.pushserver.v2.service.V2CorpConfigService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/corp")
public class V2CorpController {

    private final V2CorpConfigService corpConfigService;

    public V2CorpController(V2CorpConfigService corpConfigService) {
        this.corpConfigService = corpConfigService;
    }

    @GetMapping
    public V2Response<V2CorpResponse> getCorp(HttpSession session) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2CorpConfig config = corpConfigService.getByUserId(userId);
        V2CorpResponse response = new V2CorpResponse();
        if (config != null) {
            response.setCorpId(config.getCorpId());
        }
        return V2Response.ok(response);
    }

    @PutMapping
    public V2Response<V2CorpResponse> upsert(
            @Valid @RequestBody V2CorpConfigRequest request,
            HttpSession session
    ) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2CorpConfig config = corpConfigService.upsert(userId, request.getCorpId());
        V2CorpResponse response = new V2CorpResponse();
        response.setCorpId(config.getCorpId());
        return V2Response.ok(response);
    }
}
