package dev.qingzhou.pushserver.v2.web;

import jakarta.validation.constraints.NotBlank;

public class V2RegisterRequest {

    @NotBlank(message = "Account is required")
    private String account;

    @NotBlank(message = "Password is required")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
