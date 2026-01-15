package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.NotBlank;

public class PortalAppCreateRequest {

    @NotBlank(message = "AgentId is required")
    private String agentId;

    @NotBlank(message = "Secret is required")
    private String secret;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
