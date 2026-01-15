package dev.qingzhou.pushserver.controller.portal;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.vo.portal.PortalMessageLogResponse;
import dev.qingzhou.pushserver.service.PortalMessageLogService;
import dev.qingzhou.pushserver.service.PortalMessageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/messages")
public class PortalMessageController {

    private final PortalMessageService messageService;
    private final PortalMessageLogService messageLogService;

    public PortalMessageController(PortalMessageService messageService, PortalMessageLogService messageLogService) {
        this.messageService = messageService;
        this.messageLogService = messageLogService;
    }

    @PostMapping("/send")
    public PortalResponse<PortalMessageLogResponse> send(
            @Valid @RequestBody PortalMessageSendRequest request,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        PortalMessageLog log = messageService.send(userId, request);
        return PortalResponse.ok(toResponse(log));
    }

    @GetMapping("/logs")
    public PortalResponse<List<PortalMessageLogResponse>> logs(
            @RequestParam(defaultValue = "20") int limit,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        List<PortalMessageLogResponse> logs = messageLogService.listRecent(userId, limit).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PortalResponse.ok(logs);
    }

    private PortalMessageLogResponse toResponse(PortalMessageLog log) {
        PortalMessageLogResponse response = new PortalMessageLogResponse();
        response.setId(log.getId());
        response.setAppId(log.getAppId());
        response.setAgentId(log.getAgentId());
        response.setMsgType(log.getMsgType());
        response.setToUser(log.getToUser());
        response.setToParty(log.getToParty());
        response.setToAll(log.getToAll() != null && log.getToAll() == 1);
        response.setTitle(log.getTitle());
        response.setDescription(log.getDescription());
        response.setUrl(log.getUrl());
        response.setContent(log.getContent());
        response.setSuccess(log.getSuccess() != null && log.getSuccess() == 1);
        response.setErrorMessage(log.getErrorMessage());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}
