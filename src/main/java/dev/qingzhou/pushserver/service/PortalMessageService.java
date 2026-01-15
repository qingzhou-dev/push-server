package dev.qingzhou.pushserver.service;

import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;

public interface PortalMessageService {

    PortalMessageLog send(Long userId, PortalMessageSendRequest request);
}
