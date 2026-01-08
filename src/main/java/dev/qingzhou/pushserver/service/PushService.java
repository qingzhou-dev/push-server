package dev.qingzhou.pushserver.service;

import dev.qingzhou.push.core.api.ChannelIds;
import dev.qingzhou.push.core.api.IPushChannel;
import dev.qingzhou.push.core.api.PushChannelFactory;
import dev.qingzhou.push.core.model.PushConfig;
import dev.qingzhou.push.core.model.PushMessage;
import dev.qingzhou.push.core.model.PushResult;
import dev.qingzhou.push.core.model.enums.MessageType;
import dev.qingzhou.pushserver.config.PushProperties;
import dev.qingzhou.pushserver.web.PushRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PushService {

    private final PushProperties properties;

    public PushService(PushProperties properties) {
        this.properties = properties;
    }

    public PushResult push(PushRequest request) {
        IPushChannel channel = PushChannelFactory.getChannel(ChannelIds.WECOM);
        PushMessage message = buildMessage(request);
        return channel.send(message, buildConfig());
    }

    private PushMessage buildMessage(PushRequest request) {
        String target = requireNonBlank(request.getTarget(), "target");
        MessageType type = parseType(request.getType());
        return switch (type) {
            case TEXT -> PushMessage.text(target, requireNonBlank(request.getContent(), "content"));
            case MARKDOWN -> PushMessage.markdown(
                    target,
                    requireNonBlank(request.getTitle(), "title"),
                    requireNonBlank(request.getContent(), "content")
            );
            case TEXT_CARD -> PushMessage.textCard(
                    target,
                    requireNonBlank(request.getTitle(), "title"),
                    requireNonBlank(request.getContent(), "content"),
                    requireNonBlank(request.getUrl(), "url")
            );
            case IMAGE -> PushMessage.image(target, requireNonBlank(request.getMediaId(), "mediaId"));
            case NEWS -> PushMessage.news(target, mapArticles(request.getArticles()));
        };
    }

    private MessageType parseType(String type) {
        if (!StringUtils.hasText(type)) {
            return MessageType.TEXT;
        }
        try {
            String normalized = type.trim().toUpperCase(Locale.ROOT).replace('-', '_');
            return MessageType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported message type: " + type);
        }
    }

    private List<PushMessage.Article> mapArticles(List<PushRequest.Article> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("articles cannot be empty");
        }
        List<PushMessage.Article> articles = new ArrayList<>(items.size());
        for (PushRequest.Article item : items) {
            if (item == null) {
                throw new IllegalArgumentException("article cannot be null");
            }
            String title = requireNonBlank(item.getTitle(), "articles.title");
            String url = requireNonBlank(item.getUrl(), "articles.url");
            PushMessage.Article article = new PushMessage.Article();
            article.setTitle(title);
            article.setUrl(url);
            article.setDescription(item.getDescription());
            article.setPicUrl(item.getPicUrl());
            articles.add(article);
        }
        return articles;
    }

    private String requireNonBlank(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value.trim();
    }

    private PushConfig buildConfig() {
        PushProperties.Wecom wecom = properties.getWecom();
        return new PushConfig(
                wecom.getAppKey(),
                wecom.getAppSecret(),
                wecom.getAgentId(),
                wecom.getWebhookUrl()
        );
    }
}
