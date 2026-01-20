package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.vo.portal.PortalMessageLogConverter;
import dev.qingzhou.pushserver.model.vo.portal.PortalMessageLogResponse;
import dev.qingzhou.pushserver.model.vo.portal.PortalPageResponse;
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
@RequestMapping("/v2/messages")
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
        return PortalResponse.ok(PortalMessageLogConverter.toResponse(log));
    }

    @GetMapping("/logs")
    public PortalResponse<?> logs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) Long appId,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        boolean usePagination = page != null || pageSize != null;
        if (usePagination) {
            int resolvedPage = page == null ? 1 : page;
            int resolvedPageSize = pageSize == null ? 20 : pageSize;
            PortalPageResponse<PortalMessageLog> pagedLogs = messageLogService.pageLogs(userId, appId, success, resolvedPage, resolvedPageSize);
            List<PortalMessageLogResponse> records = pagedLogs.getRecords().stream()
                    .map(PortalMessageLogConverter::toResponse)
                    .collect(Collectors.toList());
            PortalPageResponse<PortalMessageLogResponse> response = PortalPageResponse.of(
                    records,
                    pagedLogs.getTotal(),
                    pagedLogs.getPage(),
                    pagedLogs.getPageSize()
            );
            return PortalResponse.ok(response);
        }

        List<PortalMessageLogResponse> logs = messageLogService.listRecent(userId, limit, appId, success).stream()
                .map(PortalMessageLogConverter::toResponse)
                .collect(Collectors.toList());
        return PortalResponse.ok(logs);
    }
}
