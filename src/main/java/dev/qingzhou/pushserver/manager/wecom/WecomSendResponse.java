package dev.qingzhou.pushserver.manager.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WecomSendResponse extends WecomResponse {

    @JsonProperty("invaliduser")
    private String invalidUser;

    @JsonProperty("invalidparty")
    private String invalidParty;

    @JsonProperty("invalidtag")
    private String invalidTag;

    public String getInvalidUser() {
        return invalidUser;
    }

    public void setInvalidUser(String invalidUser) {
        this.invalidUser = invalidUser;
    }

    public String getInvalidParty() {
        return invalidParty;
    }

    public void setInvalidParty(String invalidParty) {
        this.invalidParty = invalidParty;
    }

    public String getInvalidTag() {
        return invalidTag;
    }

    public void setInvalidTag(String invalidTag) {
        this.invalidTag = invalidTag;
    }
}
