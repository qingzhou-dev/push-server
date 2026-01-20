package dev.qingzhou.pushserver.controller.openapi;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.model.dto.openapi.OpenApiMessageSendRequest;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.vo.portal.PortalMessageLogConverter;
import dev.qingzhou.pushserver.model.vo.portal.PortalMessageLogResponse;
import dev.qingzhou.pushserver.service.PortalAppApiKeyService;
import dev.qingzhou.pushserver.service.PortalMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/openapi/messages")
public class OpenApiMessageController {

    private final PortalAppApiKeyService apiKeyService;
    private final PortalMessageService messageService;

    public OpenApiMessageController(
            PortalAppApiKeyService apiKeyService,
            PortalMessageService messageService
    ) {
        this.apiKeyService = apiKeyService;
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<PortalResponse<PortalMessageLogResponse>> send(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody OpenApiMessageSendRequest request
    ) {
        try {
            PortalAppApiKeyService.AppAuthContext ctx = apiKeyService.requireAppByApiKey(apiKey);
            PortalMessageSendRequest portalRequest = toPortalRequest(request, ctx.app().getId());
            PortalMessageLog log = messageService.send(ctx.app().getUserId(), portalRequest);
            return ResponseEntity.ok(PortalResponse.ok(PortalMessageLogConverter.toResponse(log)));
        } catch (PortalException ex) {
            return ResponseEntity.status(ex.getStatus().getHttpStatus())
                    .body(PortalResponse.fail(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PortalResponse.fail("Internal error"));
        }
    }

    private PortalMessageSendRequest toPortalRequest(OpenApiMessageSendRequest request, Long appId) {
        PortalMessageSendRequest target = new PortalMessageSendRequest();
        target.setAppId(appId);
        target.setToUser(request.getToUser());
        target.setToParty(request.getToParty());
        target.setToAll(request.getToAll());
        target.setMsgType(request.getMsgType());
        target.setContent(request.getContent());
        target.setTitle(request.getTitle());
        target.setDescription(request.getDescription());
        target.setUrl(request.getUrl());
        target.setBtnText(request.getBtnText());
        target.setArticles(request.getArticles());
        return target;
    }
}
