package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.Min;

public class PortalAppApiKeyUpdateRequest {

    @Min(0)
    private Integer rateLimitPerMinute;

    public Integer getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(Integer rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }
}
