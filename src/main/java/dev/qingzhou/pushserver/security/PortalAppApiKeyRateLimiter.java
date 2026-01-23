package dev.qingzhou.pushserver.security;

import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.model.entity.portal.PortalAppApiKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory fixed-window rate limiter keyed by API key record.
 */
@Component
public class PortalAppApiKeyRateLimiter {

    private static final long WINDOW_MS = 60_000L;

    private static class WindowCounter {
        private long windowStart = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger();

        synchronized boolean tryAcquire(int limit) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= WINDOW_MS) {
                windowStart = now;
                count.set(0);
            }
            int current = count.incrementAndGet();
            return current <= limit;
        }
    }

    private final Map<Long, WindowCounter> buckets = new ConcurrentHashMap<>();

    public void check(PortalAppApiKey apiKey) {
        Integer limit = apiKey.getRateLimitPerMinute();
        if (limit == null || limit <= 0) {
            return;
        }
        Long appId = apiKey.getAppId();
        if (appId == null) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "API Key 记录无效");
        }
        WindowCounter counter = buckets.computeIfAbsent(appId, ignored -> new WindowCounter());
        if (!counter.tryAcquire(limit)) {
            throw new PortalException(PortalStatus.TOO_MANY_REQUESTS, "API Key 速率限制已超出");
        }
    }

    public void evict(Long appId) {
        if (appId != null) {
            buckets.remove(appId);
        }
    }
}
