package dev.qingzhou.pushserver.v2.interceptor;

import dev.qingzhou.pushserver.v2.web.V2Exception;
import dev.qingzhou.pushserver.v2.web.V2SessionKeys;
import dev.qingzhou.pushserver.v2.web.V2Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class V2AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(V2SessionKeys.USER_ID) == null) {
            throw new V2Exception(V2Status.UNAUTHORIZED, "Unauthorized");
        }
        return true;
    }
}
