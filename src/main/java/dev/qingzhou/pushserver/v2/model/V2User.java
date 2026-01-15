package dev.qingzhou.pushserver.v2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v2_user")
public class V2User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("created_at")
    private Long createdAt;

    @TableField("updated_at")
    private Long updatedAt;
}
