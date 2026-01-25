package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.qingzhou.pushserver.mapper.portal.PortalSystemConfigMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalSystemConfig;
import dev.qingzhou.pushserver.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final String KEY_TURNSTILE_ENABLED = "turnstile.enabled";
    private static final String KEY_TURNSTILE_SITE_KEY = "turnstile.site_key";
    private static final String KEY_TURNSTILE_SECRET_KEY = "turnstile.secret_key";

    private final PortalSystemConfigMapper configMapper;

    public SystemConfigServiceImpl(PortalSystemConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    @Override
    public String get(String key) {
        return get(key, null);
    }

    @Override
    public String get(String key, String defaultValue) {
        PortalSystemConfig config = configMapper.selectOne(
                new QueryWrapper<PortalSystemConfig>().eq("config_key", key)
        );
        return config != null ? config.getConfigValue() : defaultValue;
    }

    @Override
    @Transactional
    public void set(String key, String value) {
        PortalSystemConfig config = configMapper.selectOne(
                new QueryWrapper<PortalSystemConfig>().eq("config_key", key)
        );
        if (config == null) {
            config = new PortalSystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setUpdatedAt(System.currentTimeMillis());
            configMapper.insert(config);
        } else {
            config.setConfigValue(value);
            config.setUpdatedAt(System.currentTimeMillis());
            configMapper.updateById(config);
        }
    }

    @Override
    public boolean isTurnstileEnabled() {
        return "true".equalsIgnoreCase(get(KEY_TURNSTILE_ENABLED, "false"));
    }

    @Override
    public String getTurnstileSiteKey() {
        return get(KEY_TURNSTILE_SITE_KEY, "");
    }

    @Override
    public String getTurnstileSecretKey() {
        return get(KEY_TURNSTILE_SECRET_KEY, "");
    }

    @Override
    @Transactional
    public void setTurnstileConfig(boolean enabled, String siteKey, String secretKey) {
        set(KEY_TURNSTILE_ENABLED, String.valueOf(enabled));
        set(KEY_TURNSTILE_SITE_KEY, siteKey);
        set(KEY_TURNSTILE_SECRET_KEY, secretKey);
    }
}
