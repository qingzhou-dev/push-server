package dev.qingzhou.pushserver.model.dto.portal;

import lombok.Data;

@Data
public class PortalAppUpdateRequest {
    private String secret;
    private String token;
    private String encodingAesKey;
}
