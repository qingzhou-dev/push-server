package dev.qingzhou.pushserver.model.entity.portal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v2_proxy_config")
public class PortalProxyConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String type; // HTTP, SOCKS5

    @TableField("exit_ip")
    private String exitIp;

    private Boolean active;

    @TableField("created_at")
    private Long createdAt;

    @TableField("updated_at")
    private Long updatedAt;
}
