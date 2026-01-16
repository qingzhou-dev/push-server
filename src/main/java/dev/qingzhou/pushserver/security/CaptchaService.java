package dev.qingzhou.pushserver.security;

import dev.qingzhou.pushserver.common.PortalSessionKeys;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CaptchaService {

    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;

    private final SecureRandom random = new SecureRandom();

    public String generate(HttpSession session) {
        StringBuilder builder = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = random.nextInt(CAPTCHA_CHARS.length());
            builder.append(CAPTCHA_CHARS.charAt(index));
        }
        String code = builder.toString();
        session.setAttribute(PortalSessionKeys.CAPTCHA, code);
        return code;
    }

    public void validate(HttpSession session, String input) {
        if (session == null) {
            throw new BadCredentialsException("Captcha expired");
        }
        Object stored = session.getAttribute(PortalSessionKeys.CAPTCHA);
        session.removeAttribute(PortalSessionKeys.CAPTCHA);
        if (!(stored instanceof String expected) || !StringUtils.hasText(input)) {
            throw new BadCredentialsException("Captcha invalid");
        }
        if (!expected.equalsIgnoreCase(input.trim())) {
            throw new BadCredentialsException("Captcha invalid");
        }
    }
}
