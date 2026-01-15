package dev.qingzhou.pushserver.v2.web;

import jakarta.validation.constraints.NotNull;

public class V2MessageSendRequest {

    @NotNull
    private Long appId;
    private String toUser;
    private String toParty;
    private Boolean toAll;
    private V2MessageType msgType = V2MessageType.TEXT;
    private String content;
    private String title;
    private String description;
    private String url;
    private String btnText;

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

    public V2MessageType getMsgType() {
        return msgType != null ? msgType : V2MessageType.TEXT;
    }

    public void setMsgType(V2MessageType msgType) {
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
}
