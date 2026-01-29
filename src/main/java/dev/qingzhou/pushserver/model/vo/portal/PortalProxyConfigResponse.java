package dev.qingzhou.pushserver.model.vo.portal;

import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;
import lombok.Data;

@Data
public class PortalProxyConfigResponse {
    private Long id;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String type;
    private String exitIp;
    private Boolean active;
    private Long createdAt;
    private Long updatedAt;

    public static PortalProxyConfigResponse from(PortalProxyConfig config) {
        if (config == null) {
            return null;
        }
        PortalProxyConfigResponse response = new PortalProxyConfigResponse();
        response.setId(config.getId());
        response.setHost(config.getHost());
        response.setPort(config.getPort());
        response.setUsername(config.getUsername());
        response.setPassword(config.getPassword());
        response.setType(config.getType());
        response.setExitIp(config.getExitIp());
        response.setActive(config.getActive());
        response.setCreatedAt(config.getCreatedAt());
        response.setUpdatedAt(config.getUpdatedAt());
        return response;
    }
}
