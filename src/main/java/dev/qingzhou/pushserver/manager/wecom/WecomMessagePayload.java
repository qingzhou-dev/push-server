package dev.qingzhou.pushserver.manager.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WecomMessagePayload {

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private Long agentid;
    private Text text;
    private TextCard textcard;
    private Markdown markdown;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Long getAgentid() {
        return agentid;
    }

    public void setAgentid(Long agentid) {
        this.agentid = agentid;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public TextCard getTextcard() {
        return textcard;
    }

    public void setTextcard(TextCard textcard) {
        this.textcard = textcard;
    }

    public Markdown getMarkdown() {
        return markdown;
    }

    public void setMarkdown(Markdown markdown) {
        this.markdown = markdown;
    }

    public static class Text {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class TextCard {
        private String title;
        private String description;
        private String url;
        @JsonProperty("btntxt")
        private String btnText;

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

    public static class Markdown {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
