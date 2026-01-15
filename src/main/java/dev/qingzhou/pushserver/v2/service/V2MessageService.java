package dev.qingzhou.pushserver.v2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qingzhou.pushserver.v2.model.V2CorpConfig;
import dev.qingzhou.pushserver.v2.model.V2MessageLog;
import dev.qingzhou.pushserver.v2.model.V2WecomApp;
import dev.qingzhou.pushserver.v2.wecom.V2WecomMessagePayload;
import dev.qingzhou.pushserver.v2.wecom.V2WecomSendResponse;
import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2MessageSendRequest;
import dev.qingzhou.pushserver.v2.web.V2MessageType;
import dev.qingzhou.pushserver.v2.web.V2Status;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class V2MessageService {

    private final V2WecomAppService appService;
    private final V2CorpConfigService corpConfigService;
    private final V2AccessTokenService accessTokenService;
    private final V2WecomApiClient wecomApiClient;
    private final V2MessageLogService messageLogService;
    private final ObjectMapper objectMapper;

    public V2MessageService(
            V2WecomAppService appService,
            V2CorpConfigService corpConfigService,
            V2AccessTokenService accessTokenService,
            V2WecomApiClient wecomApiClient,
            V2MessageLogService messageLogService,
            ObjectMapper objectMapper
    ) {
        this.appService = appService;
        this.corpConfigService = corpConfigService;
        this.accessTokenService = accessTokenService;
        this.wecomApiClient = wecomApiClient;
        this.messageLogService = messageLogService;
        this.objectMapper = objectMapper;
    }

    public V2MessageLog send(Long userId, V2MessageSendRequest request) {
        V2WecomApp app = appService.requireByUser(userId, request.getAppId());
        V2CorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        String accessToken = accessTokenService.getToken(app.getId(), corpConfig.getCorpId(), app.getSecret());
        V2WecomMessagePayload payload = buildPayload(app, request);
        String requestJson = toJson(payload);
        V2WecomSendResponse response = null;
        String responseJson = null;
        String errorMessage = null;
        boolean success = false;
        V2MessageLog log = null;
        try {
            response = wecomApiClient.sendMessage(accessToken, payload);
            responseJson = toJson(response);
            success = response.isSuccess();
            if (!success) {
                errorMessage = response.getErrmsg();
            }
        } catch (V2Exception ex) {
            errorMessage = ex.getMessage();
            throw ex;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            throw new V2Exception(V2Status.BAD_GATEWAY, "Failed to send message", ex);
        } finally {
            log = buildLog(userId, app, request, requestJson, responseJson, success, errorMessage);
            messageLogService.save(log);
        }
        if (!success && response != null) {
            throw new V2Exception(
                    V2Status.BAD_REQUEST,
                    "WeCom send failed: " + response.getErrmsg() + " (" + response.getErrcode() + ")"
            );
        }
        return log;
    }

    private V2WecomMessagePayload buildPayload(V2WecomApp app, V2MessageSendRequest request) {
        V2WecomMessagePayload payload = new V2WecomMessagePayload();
        payload.setMsgtype(request.getMsgType().getValue());
        payload.setAgentid(parseAgentId(app.getAgentId()));
        if (request.isToAll()) {
            payload.setTouser("@all");
        } else {
            payload.setTouser(normalizeTarget(request.getToUser()));
            payload.setToparty(normalizeTarget(request.getToParty()));
        }
        if (!request.isToAll()
                && !StringUtils.hasText(payload.getTouser())
                && !StringUtils.hasText(payload.getToparty())) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Recipient is required");
        }
        switch (request.getMsgType()) {
            case TEXT -> payload.setText(buildText(request));
            case MARKDOWN -> payload.setMarkdown(buildMarkdown(request));
            case TEXT_CARD -> payload.setTextcard(buildTextCard(request));
            default -> throw new V2Exception(V2Status.BAD_REQUEST, "Unsupported message type");
        }
        return payload;
    }

    private V2WecomMessagePayload.Text buildText(V2MessageSendRequest request) {
        String content = requireText(request.getContent(), "content");
        V2WecomMessagePayload.Text text = new V2WecomMessagePayload.Text();
        text.setContent(content);
        return text;
    }

    private V2WecomMessagePayload.Markdown buildMarkdown(V2MessageSendRequest request) {
        String content = requireText(request.getContent(), "content");
        V2WecomMessagePayload.Markdown markdown = new V2WecomMessagePayload.Markdown();
        markdown.setContent(content);
        return markdown;
    }

    private V2WecomMessagePayload.TextCard buildTextCard(V2MessageSendRequest request) {
        String title = requireText(request.getTitle(), "title");
        String description = requireText(request.getDescription(), "description");
        String url = requireText(request.getUrl(), "url");
        V2WecomMessagePayload.TextCard card = new V2WecomMessagePayload.TextCard();
        card.setTitle(title);
        card.setDescription(description);
        card.setUrl(url);
        card.setBtnText(request.getBtnText());
        return card;
    }

    private V2MessageLog buildLog(
            Long userId,
            V2WecomApp app,
            V2MessageSendRequest request,
            String requestJson,
            String responseJson,
            boolean success,
            String errorMessage
    ) {
        V2MessageLog log = new V2MessageLog();
        log.setUserId(userId);
        log.setAppId(app.getId());
        log.setAgentId(app.getAgentId());
        log.setMsgType(request.getMsgType().getValue());
        log.setToUser(request.getToUser());
        log.setToParty(request.getToParty());
        log.setToAll(request.isToAll() ? 1 : 0);
        log.setTitle(request.getTitle());
        log.setDescription(request.getDescription());
        log.setUrl(request.getUrl());
        if (request.getMsgType() == V2MessageType.TEXT || request.getMsgType() == V2MessageType.MARKDOWN) {
            log.setContent(request.getContent());
        }
        log.setRequestJson(requestJson);
        log.setResponseJson(responseJson);
        log.setSuccess(success ? 1 : 0);
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(System.currentTimeMillis());
        return log;
    }

    private String normalizeTarget(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new V2Exception(V2Status.BAD_REQUEST, field + " is required");
        }
        return value.trim();
    }

    private long parseAgentId(String agentId) {
        try {
            return Long.parseLong(agentId.trim());
        } catch (Exception ex) {
            throw new V2Exception(V2Status.BAD_REQUEST, "Invalid agentId");
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return null;
        }
    }
}
