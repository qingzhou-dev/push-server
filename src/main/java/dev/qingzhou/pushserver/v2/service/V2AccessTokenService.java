package dev.qingzhou.pushserver.v2.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.qingzhou.pushserver.v2.wecom.V2WecomToken;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class V2AccessTokenService {

    private static final int DEFAULT_EXPIRES_IN = 7200;
    private static final long EXPIRE_BUFFER_MILLIS = 60_000L;

    private final V2WecomApiClient wecomApiClient;
    private final Cache<Long, CachedToken> cache;

    public V2AccessTokenService(V2WecomApiClient wecomApiClient) {
        this.wecomApiClient = wecomApiClient;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build();
    }

    public V2WecomToken fetchToken(String corpId, String secret) {
        return wecomApiClient.getToken(corpId, secret);
    }

    public String getToken(Long appId, String corpId, String secret) {
        CachedToken cached = cache.getIfPresent(appId);
        long now = System.currentTimeMillis();
        if (cached != null && cached.expireAtMillis > now) {
            return cached.value;
        }
        V2WecomToken token = fetchToken(corpId, secret);
        int expiresIn = token.getExpiresIn() != null ? token.getExpiresIn() : DEFAULT_EXPIRES_IN;
        long expireAt = now + TimeUnit.SECONDS.toMillis(expiresIn) - EXPIRE_BUFFER_MILLIS;
        if (expireAt < now) {
            expireAt = now;
        }
        cache.put(appId, new CachedToken(token.getAccessToken(), expireAt));
        return token.getAccessToken();
    }

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
