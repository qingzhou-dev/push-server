package dev.qingzhou.pushserver.model.dto.openapi;

import dev.qingzhou.pushserver.model.dto.portal.PortalMessageSendRequest;
import dev.qingzhou.pushserver.model.dto.portal.PortalMessageType;
import java.util.List;

public class OpenApiMessageSendRequest {

    // Compatibility fields for v1
    private String target;
    private String type;

    // v2 fields
    private String toUser;
    private String toParty;
    private Boolean toAll;
    private PortalMessageType msgType = PortalMessageType.TEXT;
    private String content;
    private String title;
    private String description;
    private String url;
    private String btnText;
    private List<PortalMessageSendRequest.PortalNewsArticle> articles;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (type != null) {
            try {
                this.msgType = PortalMessageType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid type string, fallback to default or existing msgType
            }
        }
    }

    public String getToUser() {
        String effectiveUser = (toUser == null && target != null) ? target : toUser;
        if ("@all".equalsIgnoreCase(effectiveUser)) {
            return null; // Handled by getToAll()
        }
        return effectiveUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getToParty() {
        return toParty;
    }

    public void setToParty(String toParty) {
        this.toParty = toParty;
    }

    public boolean isToAll() {
        return Boolean.TRUE.equals(getToAll());
    }

    public Boolean getToAll() {
        if (Boolean.TRUE.equals(toAll)) {
            return true;
        }
        // Support "@all" in target or toUser
        if ("@all".equalsIgnoreCase(target) || "@all".equalsIgnoreCase(toUser)) {
            return true;
        }
        return false;
    }

    public void setToAll(Boolean toAll) {
        this.toAll = toAll;
    }

    public PortalMessageType getMsgType() {
        return msgType != null ? msgType : PortalMessageType.TEXT;
    }

    public void setMsgType(PortalMessageType msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public List<PortalMessageSendRequest.PortalNewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<PortalMessageSendRequest.PortalNewsArticle> articles) {
        this.articles = articles;
    }
}
