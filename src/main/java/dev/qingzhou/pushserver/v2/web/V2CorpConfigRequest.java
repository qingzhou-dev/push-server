package dev.qingzhou.pushserver.v2.web;

import jakarta.validation.constraints.NotBlank;

public class V2CorpConfigRequest {

    @NotBlank(message = "CorpId is required")
    private String corpId;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }
}
