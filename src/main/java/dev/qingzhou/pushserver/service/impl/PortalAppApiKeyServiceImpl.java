package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.exception.PortalException;
import dev.qingzhou.pushserver.exception.PortalStatus;
import dev.qingzhou.pushserver.mapper.portal.PortalAppApiKeyMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalAppApiKey;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.service.PortalAppApiKeyService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import dev.qingzhou.pushserver.security.PortalAppApiKeyRateLimiter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PortalAppApiKeyServiceImpl extends ServiceImpl<PortalAppApiKeyMapper, PortalAppApiKey>
        implements PortalAppApiKeyService {

    private final PortalWecomAppService appService;
    private final PortalAppApiKeyRateLimiter rateLimiter;
    private final SecureRandom random = new SecureRandom();

    public PortalAppApiKeyServiceImpl(PortalWecomAppService appService, PortalAppApiKeyRateLimiter rateLimiter) {
        this.appService = appService;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public PortalAppApiKey rotateKey(Long userId, Long appId) {
        PortalWecomApp app = appService.requireByUser(userId, appId);
        String rawKey = generateKey();
        String hash = hashKey(rawKey);
        PortalAppApiKey existing = getOne(new QueryWrapper<PortalAppApiKey>()
                .eq("app_id", app.getId()));
        long now = System.currentTimeMillis();
        if (existing == null) {
            PortalAppApiKey record = new PortalAppApiKey();
            record.setAppId(app.getId());
            record.setApiKeyHash(hash);
            record.setApiKeyPlain(rawKey);
            record.setRateLimitPerMinute(0);
            record.setCreatedAt(now);
            record.setUpdatedAt(now);
            save(record);
            return record;
        }
        existing.setApiKeyHash(hash);
        existing.setApiKeyPlain(rawKey);
        existing.setUpdatedAt(now);
        updateById(existing);
        return existing;
    }

    @Override
    public PortalAppApiKey findByAppId(Long userId, Long appId) {
        PortalWecomApp app = appService.requireByUser(userId, appId);
        return getOne(new QueryWrapper<PortalAppApiKey>()
                .eq("app_id", app.getId()));
    }

    @Override
    public void removeByAppId(Long appId) {
        remove(new QueryWrapper<PortalAppApiKey>()
                .eq("app_id", appId));
        rateLimiter.evict(appId);
    }

    @Override
    public PortalAppApiKey updateRateLimit(Long userId, Long appId, Integer rateLimitPerMinute) {
        if (rateLimitPerMinute != null && rateLimitPerMinute < 0) {
            throw new PortalException(PortalStatus.BAD_REQUEST, "rateLimitPerMinute must be >= 0");
        }
        PortalWecomApp app = appService.requireByUser(userId, appId);
        PortalAppApiKey record = getOne(new QueryWrapper<PortalAppApiKey>()
                .eq("app_id", app.getId()));
        if (record == null) {
            throw new PortalException(PortalStatus.NOT_FOUND, "API key not found");
        }
        record.setRateLimitPerMinute(rateLimitPerMinute == null ? 0 : rateLimitPerMinute);
        record.setUpdatedAt(System.currentTimeMillis());
        updateById(record);
        return record;
    }

    @Override
    public AppAuthContext requireAppByApiKey(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Missing API key");
        }
        String hash = hashKey(apiKey.trim());
        PortalAppApiKey record = getOne(new QueryWrapper<PortalAppApiKey>()
                .eq("api_key_hash", hash));
        if (record == null) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Invalid API key");
        }
        PortalWecomApp app = appService.getById(record.getAppId());
        if (app == null) {
            throw new PortalException(PortalStatus.UNAUTHORIZED, "Invalid API key");
        }
        rateLimiter.check(record);
        return new AppAuthContext(record, app);
    }

    private String generateKey() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashKey(String key) {
        MessageDigest digest = getDigest();
        byte[] hashed = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hashed);
    }

    private MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
