package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PortalInitRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    private boolean turnstileEnabled;
    private String turnstileSiteKey;
    private String turnstileSecretKey;
}
