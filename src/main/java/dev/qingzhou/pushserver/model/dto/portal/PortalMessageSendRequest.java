package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PortalMessageSendRequest {

    @NotNull(message = "应用 ID 不能为空")
    private Long appId;
    private String toUser;
    private String toParty;
    private Boolean toAll;
    private PortalMessageType msgType = PortalMessageType.TEXT;
    private String content;
    private String title;
    private String description;
    private String url;
    private String btnText;
    private List<PortalNewsArticle> articles;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getToUser() {
        return toUser;
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
        return Boolean.TRUE.equals(toAll);
    }

    public Boolean getToAll() {
        return toAll;
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

    public List<PortalNewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<PortalNewsArticle> articles) {
        this.articles = articles;
    }

    public static class PortalNewsArticle {
        private String title;
        private String description;
        private String url;
        private String picUrl;

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

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}
