package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import java.util.List;

public interface PortalMessageLogService extends IService<PortalMessageLog> {

    List<PortalMessageLog> listRecent(Long userId, int limit);
}
