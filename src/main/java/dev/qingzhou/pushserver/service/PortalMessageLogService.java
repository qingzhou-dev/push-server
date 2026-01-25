package dev.qingzhou.pushserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.vo.portal.PortalPageResponse;
import java.util.List;

public interface PortalMessageLogService extends IService<PortalMessageLog> {

    List<PortalMessageLog> listRecent(Long userId, int limit, Long appId, Boolean success);

    PortalPageResponse<PortalMessageLog> pageLogs(Long userId, Long appId, Boolean success, int page, int pageSize);
}
