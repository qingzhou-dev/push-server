package dev.qingzhou.pushserver.v2.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class V2WecomResponse {

    @JsonProperty("errcode")
    private Integer errcode;

    @JsonProperty("errmsg")
    private String errmsg;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isSuccess() {
        return errcode != null && errcode == 0;
    }
}
