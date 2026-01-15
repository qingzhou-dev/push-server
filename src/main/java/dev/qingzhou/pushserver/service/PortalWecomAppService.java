package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import java.util.List;

public interface PortalWecomAppService extends IService<PortalWecomApp> {

    PortalWecomApp addApp(Long userId, String agentId, String secret);

    List<PortalWecomApp> listByUser(Long userId);

    PortalWecomApp requireByUser(Long userId, Long appId);

    PortalWecomApp syncApp(Long userId, Long appId);

    void deleteApp(Long userId, Long appId);
}
