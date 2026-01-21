package dev.qingzhou.pushserver.security;

import dev.qingzhou.pushserver.service.SystemConfigService;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
public class CaptchaService {

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
    private final SystemConfigService configService;
    private final RestClient restClient;

    public CaptchaService(SystemConfigService configService) {
        this.configService = configService;
        this.restClient = RestClient.create();
    }

    public void validate(String input) {
        if (!configService.isTurnstileEnabled()) {
            return;
        }

        if (!StringUtils.hasText(input)) {
            throw new BadCredentialsException("Captcha token is required");
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", configService.getTurnstileSecretKey());
        formData.add("response", input);

        Map result = restClient.post()
                .uri(VERIFY_URL)
                .body(formData)
                .retrieve()
                .body(Map.class);

        if (result == null || !Boolean.TRUE.equals(result.get("success"))) {
            throw new BadCredentialsException("Captcha verification failed");
        }
    }
}