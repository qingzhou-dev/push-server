package dev.qingzhou.pushserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.manager.wecom.WecomApiClient;
import dev.qingzhou.pushserver.manager.wecom.WecomMessagePayload;
import dev.qingzhou.pushserver.manager.wecom.WecomSendResponse;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageType;
import dev.qingzhou.pushserver.model.entity.portal.PortalCorpConfig;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.service.PortalAccessTokenService;
import dev.qingzhou.pushserver.service.PortalCorpConfigService;
import dev.qingzhou.pushserver.service.PortalMessageLogService;
import dev.qingzhou.pushserver.service.PortalMessageService;
import dev.qingzhou.pushserver.service.PortalProxyConfigService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalMessageServiceImpl implements PortalMessageService {

    private final PortalWecomAppService appService;
    private final PortalCorpConfigService corpConfigService;
    private final PortalAccessTokenService accessTokenService;
    private final WecomApiClient wecomApiClient;
    private final PortalMessageLogService messageLogService;
    private final ObjectMapper objectMapper;
    private final PortalProxyConfigService proxyConfigService;

    public PortalMessageServiceImpl(
            PortalWecomAppService appService,
            PortalCorpConfigService corpConfigService,
            PortalAccessTokenService accessTokenService,
            WecomApiClient wecomApiClient,
            PortalMessageLogService messageLogService,
            ObjectMapper objectMapper,
            PortalProxyConfigService proxyConfigService
    ) {
        this.appService = appService;
        this.corpConfigService = corpConfigService;
        this.accessTokenService = accessTokenService;
        this.wecomApiClient = wecomApiClient;
        this.messageLogService = messageLogService;
        this.objectMapper = objectMapper;
        this.proxyConfigService = proxyConfigService;
    }

    @Override
    public PortalMessageLog send(Long userId, PortalMessageSendRequest request) {
        PortalWecomApp app = appService.requireByUser(userId, request.getAppId());
        PortalCorpConfig corpConfig = corpConfigService.requireByUserId(userId);
        PortalProxyConfig proxyConfig = proxyConfigService.getByUserId(userId);
        
        String accessToken = accessTokenService.getToken(app.getId(), corpConfig.getCorpId(), app.getSecret(), proxyConfig);
        WecomMessagePayload payload = buildPayload(app, request);
        String requestJson = toJson(payload);
        WecomSendResponse response = null;
        String responseJson = null;
        String errorMessage = null;
        boolean success = false;
        PortalMessageLog log = null;
        try {
            response = wecomApiClient.sendMessage(accessToken, payload, proxyConfig);
            responseJson = toJson(response);
            success = response.isSuccess();
            if (!success) {
                errorMessage = response.getErrmsg();
            }
        } catch (PortalException ex) {
            errorMessage = ex.getMessage();
            throw ex;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            throw new PortalException(PortalStatus.BAD_GATEWAY, "发送消息失败", ex);
        } finally {
            log = buildLog(userId, app, request, requestJson, responseJson, success, errorMessage);
            messageLogService.save(log);
        }
        if (!success && response != null) {
            throw new PortalException(
                    PortalStatus.BAD_REQUEST,
                    "企业微信发送失败: " + response.getErrmsg() + " (" + response.getErrcode() + ")"
            );
        }
        return log;
    }

    private WecomMessagePayload buildPayload(PortalWecomApp app, PortalMessageSendRequest request) {
        WecomMessagePayload payload = new WecomMessagePayload();
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
            throw new PortalException(PortalStatus.BAD_REQUEST, "接收者不能为空");
        }
        switch (request.getMsgType()) {
            case TEXT -> payload.setText(buildText(request));
            case MARKDOWN -> payload.setMarkdown(buildMarkdown(request));
            case TEXT_CARD -> payload.setTextcard(buildTextCard(request));
            case NEWS -> payload.setNews(buildNews(request));
            default -> throw new PortalException(PortalStatus.BAD_REQUEST, "不支持的消息类型");
        }
        return payload;
    }

    private WecomMessagePayload.Text buildText(PortalMessageSendRequest request) {
        String content = requireText(request.getContent(), "content");
        WecomMessagePayload.Text text = new WecomMessagePayload.Text();
        text.setContent(content);
        return text;
    }

    private WecomMessagePayload.Markdown buildMarkdown(PortalMessageSendRequest request) {
        String content = requireText(request.getContent(), "content");
        WecomMessagePayload.Markdown markdown = new WecomMessagePayload.Markdown();
        markdown.setContent(content);
        return markdown;
    }

    private WecomMessagePayload.TextCard buildTextCard(PortalMessageSendRequest request) {
        String title = requireText(request.getTitle(), "title");
        String description = requireText(request.getDescription(), "description");
        String url = requireText(request.getUrl(), "url");
        WecomMessagePayload.TextCard card = new WecomMessagePayload.TextCard();
        card.setTitle(title);
        card.setDescription(description);
        card.setUrl(url);
        card.setBtnText(request.getBtnText());
        return card;
    }

    private WecomMessagePayload.News buildNews(PortalMessageSendRequest request) {
        List<PortalMessageSendRequest.PortalNewsArticle> items = request.getArticles();
        if (items == null || items.isEmpty()) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "articles 不能为空");
        }
        List<WecomMessagePayload.Article> articles = new ArrayList<>(items.size());
        for (PortalMessageSendRequest.PortalNewsArticle item : items) {
            if (item == null) {
                throw new PortalException(PortalStatus.BAD_REQUEST, "article 不能为空");
            }
            WecomMessagePayload.Article article = new WecomMessagePayload.Article();
            article.setTitle(requireText(item.getTitle(), "articles.title"));
            article.setUrl(requireText(item.getUrl(), "articles.url"));
            if (StringUtils.hasText(item.getDescription())) {
                article.setDescription(item.getDescription().trim());
            }
            if (StringUtils.hasText(item.getPicUrl())) {
                article.setPicUrl(item.getPicUrl().trim());
            }
            articles.add(article);
        }
        WecomMessagePayload.News news = new WecomMessagePayload.News();
        news.setArticles(articles);
        return news;
    }

    private PortalMessageLog buildLog(
            Long userId,
            PortalWecomApp app,
            PortalMessageSendRequest request,
            String requestJson,
            String responseJson,
            boolean success,
            String errorMessage
    ) {
        PortalMessageLog log = new PortalMessageLog();
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
        if (request.getMsgType() == PortalMessageType.TEXT || request.getMsgType() == PortalMessageType.MARKDOWN) {
            log.setContent(request.getContent());
        } else if (request.getMsgType() == PortalMessageType.NEWS && request.getArticles() != null
                && !request.getArticles().isEmpty()) {
            PortalMessageSendRequest.PortalNewsArticle first = request.getArticles().get(0);
            log.setTitle(first.getTitle());
            log.setDescription(first.getDescription());
            log.setUrl(first.getUrl());
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
            throw new PortalException(PortalStatus.BAD_REQUEST, field + " 不能为空");
        }
        return value.trim();
    }

    private long parseAgentId(String agentId) {
        try {
            return Long.parseLong(agentId.trim());
        } catch (Exception ex) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "无效的 agentId");
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
