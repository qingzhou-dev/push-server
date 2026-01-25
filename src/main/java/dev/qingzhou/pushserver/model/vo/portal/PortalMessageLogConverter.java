package dev.qingzhou.pushserver.model.vo.portal;

import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;

public final class PortalMessageLogConverter {

    private PortalMessageLogConverter() {
    }

    public static PortalMessageLogResponse toResponse(PortalMessageLog log) {
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
