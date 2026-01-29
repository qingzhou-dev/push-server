package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.manager.wecom.WecomApiClient;
import dev.qingzhou.pushserver.mapper.portal.PortalProxyConfigMapper;
import dev.qingzhou.pushserver.model.dto.portal.PortalProxyConfigRequest;
import dev.qingzhou.pushserver.model.entity.portal.PortalProxyConfig;
import dev.qingzhou.pushserver.service.PortalProxyConfigService;
import org.springframework.stereotype.Service;

@Service
public class PortalProxyConfigServiceImpl extends ServiceImpl<PortalProxyConfigMapper, PortalProxyConfig> implements PortalProxyConfigService {

    private final WecomApiClient wecomApiClient;

    public PortalProxyConfigServiceImpl(WecomApiClient wecomApiClient) {
        this.wecomApiClient = wecomApiClient;
    }

    @Override
    public PortalProxyConfig getByUserId(Long userId) {
        QueryWrapper<PortalProxyConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return getOne(wrapper);
    }

    @Override
    public PortalProxyConfig upsert(Long userId, PortalProxyConfigRequest request) {
        PortalProxyConfig config = getByUserId(userId);
        long now = System.currentTimeMillis();
        if (config == null) {
            config = new PortalProxyConfig();
            config.setUserId(userId);
            config.setCreatedAt(now);
        }
        
        config.setHost(request.getHost());
        config.setPort(request.getPort());
        config.setUsername(request.getUsername());
        config.setPassword(request.getPassword());
        config.setType(request.getType());
        config.setExitIp(request.getExitIp());
        config.setActive(request.getActive());
        config.setUpdatedAt(now);

        // 1. 先保存配置到数据库
        if (config.getId() == null) {
            save(config);
        } else {
            updateById(config);
        }

        // 2. 再进行连通性测试（如果启用）
        // 即使测试失败抛出异常，数据也已经保存成功，方便用户后续修改
        if (Boolean.TRUE.equals(config.getActive())) {
            wecomApiClient.testConnectivity(config);
        }
        
        return config;
    }

    @Override
    public void deleteByUserId(Long userId) {
        QueryWrapper<PortalProxyConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        remove(wrapper);
    }
}
