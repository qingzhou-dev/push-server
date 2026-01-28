package dev.qingzhou.pushserver.manager.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WecomMessagePayload {

    // ==========================================
    // 发送消息字段 (JSON)
    // ==========================================
    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private Long agentid;
    private Integer safe;
    
    @JsonProperty("enable_id_trans")
    private Integer enableIdTrans;
    
    @JsonProperty("enable_duplicate_check")
    private Integer enableDuplicateCheck;
    
    @JsonProperty("duplicate_check_interval")
    private Integer duplicateCheckInterval;

    private Text text;
    private Markdown markdown;
    private TextCard textcard;
    private News news;

    // ==========================================
    // 接收回调字段 (XML 解析后赋值)
    // ==========================================
    private String toUserName;
    private String fromUserName;
    private Long createTime;
    // 注意：接收时的 MsgType 和发送时的 msgtype 可能大小写不同，
    // 但通常我们可以复用 msgtype 字段，或者分开。
    // 为了不破坏现有发送逻辑，发送用 msgtype (全小写)。
    // 接收到的 XML MsgType 也是 "text" 等小写 (在 XML 值里)，但标签是 PascalCase。
    // 这里我们额外定义字段用于接收，避免混淆
    private String receiveMsgType; 
    
    private String content; // 接收到的文本内容
    private Long msgId;
    private String receiveAgentId; // 接收到的 AgentID
    private String event;
    private String eventKey;
    
    // 图片消息字段
    private String picUrl;
    private String mediaId;

    // ==========================================
    // 内部类定义 (用于发送消息)
    // ==========================================

    @Data
    public static class Text {
        private String content;
    }

    @Data
    public static class Markdown {
        private String content;
    }

    @Data
    public static class TextCard {
        private String title;
        private String description;
        private String url;
        @JsonProperty("btntxt")
        private String btnText;
    }

    @Data
    public static class News {
        private List<Article> articles;
    }

    @Data
    public static class Article {
        private String title;
        private String description;
        private String url;
        @JsonProperty("picurl")
        private String picUrl;
    }
}
