package dev.qingzhou.pushserver.v2.web;

import jakarta.servlet.http.HttpSession;

public final class V2SessionSupport {

    private V2SessionSupport() {
    }

    public static Long requireUserId(HttpSession session) {
        if (session == null) {
            throw new V2Exception(V2Status.UNAUTHORIZED, "Unauthorized");
        }
        Object value = session.getAttribute(V2SessionKeys.USER_ID);
        if (value instanceof Long userId) {
            return userId;
        }
        throw new V2Exception(V2Status.UNAUTHORIZED, "Unauthorized");
    }
}
