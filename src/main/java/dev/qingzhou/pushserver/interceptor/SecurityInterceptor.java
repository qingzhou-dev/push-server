package dev.qingzhou.pushserver.interceptor;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.qingzhou.pushserver.config.PushProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

    private final PushProperties properties;
    private final int maxFails;

    // 1. 封禁名单缓存：Key=IP, Value=封禁截止时间戳
    private final Cache<String, Long> blockList;

    // 2. 失败计数缓存：Key=IP, Value=失败次数
    private final Cache<String, Integer> failCounts;

    public SecurityInterceptor(PushProperties properties) {
        this.properties = properties;
        PushProperties.Security security = properties.getSecurity();
        this.blockList = Caffeine.newBuilder()
                .expireAfterWrite(security.getBlockMinutes(), TimeUnit.MINUTES)
                .build();
        this.failCounts = Caffeine.newBuilder()
                .expireAfterWrite(security.getFailWindowMinutes(), TimeUnit.MINUTES)
                .build();
        this.maxFails = security.getMaxFails();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);

        // --- 1. 检查是否在小黑屋 ---
        if (blockList.getIfPresent(clientIp) != null) {
            log.warn("Blocked request from IP: {}", clientIp);
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("IP Blocked due to too many failed attempts.");
            return false;
        }

        // --- 2. 执行鉴权逻辑 ---
        String apiKey = request.getHeader("X-API-Key");
        if (!isAuthorized(apiKey)) {
            // 鉴权失败 -> 计数器 +1
            Integer count = failCounts.get(clientIp, k -> 0);
            failCounts.put(clientIp, count + 1);
            log.warn("Auth failed for IP: {}, count: {}", clientIp, count + 1);
            if (count + 1 >= maxFails) {
                blockList.put(clientIp, System.currentTimeMillis());
                log.error("IP {} has been blocked due to brute force attempts.", clientIp);
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return false;
        }
        return true;
    }

    private boolean isAuthorized(String apiKey) {
        return StringUtils.hasText(apiKey) && apiKey.equals(properties.getAuth().getKey());
    }

    // 获取真实IP（如果前面有Nginx，需要取 X-Forwarded-For）
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
