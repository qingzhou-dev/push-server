package dev.qingzhou.pushserver.model.entity.portal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v2_app_api_key")
public class PortalAppApiKey {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("app_id")
    private Long appId;

    @TableField("api_key_hash")
    private String apiKeyHash;

    @TableField("api_key_plain")
    private String apiKeyPlain;

    @TableField("rate_limit_per_minute")
    private Integer rateLimitPerMinute;

    @TableField("created_at")
    private Long createdAt;

    @TableField("updated_at")
    private Long updatedAt;
}
