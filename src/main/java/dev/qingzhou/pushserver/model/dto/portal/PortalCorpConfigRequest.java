package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.NotBlank;

public class PortalCorpConfigRequest {

    @NotBlank(message = "CorpId is required")
    private String corpId;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }
}
