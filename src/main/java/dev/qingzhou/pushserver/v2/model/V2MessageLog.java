package dev.qingzhou.pushserver.v2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v2_message_log")
public class V2MessageLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("app_id")
    private Long appId;

    @TableField("agent_id")
    private String agentId;

    @TableField("msg_type")
    private String msgType;

    @TableField("to_user")
    private String toUser;

    @TableField("to_party")
    private String toParty;

    @TableField("to_all")
    private Integer toAll;

    private String title;

    private String description;

    private String url;

    private String content;

    @TableField("request_json")
    private String requestJson;

    @TableField("response_json")
    private String responseJson;

    private Integer success;

    @TableField("error_message")
    private String errorMessage;

    @TableField("created_at")
    private Long createdAt;
}
