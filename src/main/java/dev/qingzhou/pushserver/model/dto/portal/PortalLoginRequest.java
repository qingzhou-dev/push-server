package dev.qingzhou.pushserver.model.dto.portal;

import jakarta.validation.constraints.NotBlank;

public class PortalLoginRequest {

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
