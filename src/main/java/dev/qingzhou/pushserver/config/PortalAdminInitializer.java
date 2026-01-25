package dev.qingzhou.pushserver.config;

import dev.qingzhou.pushserver.model.entity.portal.PortalUser;
import dev.qingzhou.pushserver.service.PortalUserService;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//@Component
public class PortalAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PortalAdminInitializer.class);

    private static final String ADMIN_ACCOUNT = "admin";
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 12;

    private final PortalUserService portalUserService;
    private final SecureRandom random = new SecureRandom();

    public PortalAdminInitializer(PortalUserService portalUserService) {
        this.portalUserService = portalUserService;
    }

    @Override
    public void run(ApplicationArguments args) {
        PortalUser existing = portalUserService.findByAccount(ADMIN_ACCOUNT);
        if (existing != null) {
            return;
        }
        String password = generatePassword();
        portalUserService.register(ADMIN_ACCOUNT, password);
        log.info("Initialized admin user: account={}, password={}", ADMIN_ACCOUNT, password);
    }

    private String generatePassword() {
        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            builder.append(PASSWORD_CHARS.charAt(index));
        }
        return builder.toString();
    }
}
