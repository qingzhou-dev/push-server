package dev.qingzhou.pushserver.common;

import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import jakarta.servlet.http.HttpSession;

public final class PortalSessionSupport {

    private PortalSessionSupport() {
    }

    public static Long requireUserId(HttpSession session) {
        if (session == null) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Unauthorized");
        }
        Object value = session.getAttribute(PortalSessionKeys.USER_ID);
        if (value instanceof Long userId) {
            return userId;
        }
        throw new PortalException(PortalStatus.UNAUTHORIZED, "Unauthorized");
    }
}
