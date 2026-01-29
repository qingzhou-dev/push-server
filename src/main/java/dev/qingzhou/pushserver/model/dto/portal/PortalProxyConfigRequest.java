package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PortalProxyConfigRequest {

    @NotBlank(message = "服务器地址不能为空")
    private String host;

    @NotNull(message = "端口不能为空")
    @Min(value = 1, message = "端口范围无效")
    @Max(value = 65535, message = "端口范围无效")
    private Integer port;

    private String username;

    private String password;

    private String type = "HTTP";

    private String exitIp;

    private Boolean active = true;
}
