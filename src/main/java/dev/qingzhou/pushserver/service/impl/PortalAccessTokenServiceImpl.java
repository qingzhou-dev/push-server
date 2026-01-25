package dev.qingzhou.pushserver.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.qingzhou.pushserver.manager.wecom.WecomApiClient;
import dev.qingzhou.pushserver.manager.wecom.WecomToken;
import dev.qingzhou.pushserver.service.PortalAccessTokenService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class PortalAccessTokenServiceImpl implements PortalAccessTokenService {

    private static final int DEFAULT_EXPIRES_IN = 7200;
    private static final long EXPIRE_BUFFER_MILLIS = 60_000L;

    private final WecomApiClient wecomApiClient;
    private final Cache<Long, CachedToken> cache;

    public PortalAccessTokenServiceImpl(WecomApiClient wecomApiClient) {
        this.wecomApiClient = wecomApiClient;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build();
    }

    @Override
    public WecomToken fetchToken(String corpId, String secret) {
        return wecomApiClient.getToken(corpId, secret);
    }

    @Override
    public String getToken(Long appId, String corpId, String secret) {
        CachedToken cached = cache.getIfPresent(appId);
        long now = System.currentTimeMillis();
        if (cached != null && cached.expireAtMillis > now) {
            return cached.value;
        }
        WecomToken token = fetchToken(corpId, secret);
        int expiresIn = token.getExpiresIn() != null ? token.getExpiresIn() : DEFAULT_EXPIRES_IN;
        long expireAt = now + TimeUnit.SECONDS.toMillis(expiresIn) - EXPIRE_BUFFER_MILLIS;
        if (expireAt < now) {
            expireAt = now;
        }
        cache.put(appId, new CachedToken(token.getAccessToken(), expireAt));
        return token.getAccessToken();
    }

    @Override
    public void evict(Long appId) {
        cache.invalidate(appId);
    }

    private static class CachedToken {
        private final String value;
        private final long expireAtMillis;

        private CachedToken(String value, long expireAtMillis) {
            this.value = value;
            this.expireAtMillis = expireAtMillis;
        }
    }
}
