package dev.qingzhou.pushserver.manager.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WecomAgentInfo extends WecomResponse {

    private String name;

    private String description;

    @JsonProperty("square_logo_url")
    private String avatarUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
