package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PortalProxyConfigRequest {

    @NotBlank(message = "Host is required")
    private String host;

    @NotNull(message = "Port is required")
    @Min(1)
    @Max(65535)
    private Integer port;

    private String username;

    private String password;

    private String type = "HTTP";

    private String exitIp;

    private Boolean active = true;
}
