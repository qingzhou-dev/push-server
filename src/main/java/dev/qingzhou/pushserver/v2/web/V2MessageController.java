package dev.qingzhou.pushserver.v2.web;

import dev.qingzhou.pushserver.v2.model.V2MessageLog;
import dev.qingzhou.pushserver.v2.service.V2MessageLogService;
import dev.qingzhou.pushserver.v2.service.V2MessageService;
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
@RequestMapping("/v2/messages")
public class V2MessageController {

    private final V2MessageService messageService;
    private final V2MessageLogService messageLogService;

    public V2MessageController(V2MessageService messageService, V2MessageLogService messageLogService) {
        this.messageService = messageService;
        this.messageLogService = messageLogService;
    }

    @PostMapping("/send")
    public V2Response<V2MessageLogResponse> send(
            @Valid @RequestBody V2MessageSendRequest request,
            HttpSession session
    ) {
        Long userId = V2SessionSupport.requireUserId(session);
        V2MessageLog log = messageService.send(userId, request);
        return V2Response.ok(toResponse(log));
    }

    @GetMapping("/logs")
    public V2Response<List<V2MessageLogResponse>> logs(
            @RequestParam(defaultValue = "20") int limit,
            HttpSession session
    ) {
        Long userId = V2SessionSupport.requireUserId(session);
        List<V2MessageLogResponse> logs = messageLogService.listRecent(userId, limit).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return V2Response.ok(logs);
    }

    private V2MessageLogResponse toResponse(V2MessageLog log) {
        V2MessageLogResponse response = new V2MessageLogResponse();
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
