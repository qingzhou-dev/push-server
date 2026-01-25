package dev.qingzhou.pushserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qingzhou.pushserver.model.dto.portal.PortalLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
// 移除不需要的 import
// import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

public class PortalJsonLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final CaptchaService captchaService;

    public PortalJsonLoginAuthenticationFilter(
            ObjectMapper objectMapper,
            CaptchaService captchaService
    ) {
        super("/api/login");
        this.objectMapper = objectMapper;
        this.captchaService = captchaService;
    }

    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) throws AuthenticationException {
        if (!isJsonRequest(request)) {
            throw new AuthenticationServiceException("不支持的内容类型");
        }
        PortalLoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), PortalLoginRequest.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new AuthenticationServiceException("无效的登录参数", ex);
        }

        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        String captcha = loginRequest.getCaptcha();

        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            throw new AuthenticationServiceException("账号和密码不能为空");
        }

        captchaService.validate(captcha);
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(account, password);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}