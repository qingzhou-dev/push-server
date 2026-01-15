package dev.qingzhou.pushserver.aspect;

import dev.qingzhou.pushserver.common.PortalSessionKeys;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PortalAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(PortalSessionKeys.USER_ID) == null) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Unauthorized");
        }
        return true;
    }
}
