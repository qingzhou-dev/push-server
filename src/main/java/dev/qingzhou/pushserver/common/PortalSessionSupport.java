package dev.qingzhou.pushserver.common;

import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.security.PortalUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class PortalSessionSupport {

    private PortalSessionSupport() {
    }

    public static Long requireUserId(HttpSession session) {
        if (session != null) {
            Object value = session.getAttribute(PortalSessionKeys.USER_ID);
            if (value instanceof Long userId) {
                return userId;
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof PortalUserDetails userDetails) {
                Long userId = userDetails.getUserId();
                if (session != null) {
                    session.setAttribute(PortalSessionKeys.USER_ID, userId);
                }
                return userId;
            }
            if (principal instanceof Long userId) {
                if (session != null) {
                    session.setAttribute(PortalSessionKeys.USER_ID, userId);
                }
                return userId;
            }
        }
        throw new PortalException(PortalStatus.UNAUTHORIZED, "未授权");
    }
}
