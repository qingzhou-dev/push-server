package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.mapper.portal.PortalMessageLogMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.service.PortalMessageLogService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PortalMessageLogServiceImpl extends ServiceImpl<PortalMessageLogMapper, PortalMessageLog> implements PortalMessageLogService {

    @Override
    public List<PortalMessageLog> listRecent(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return lambdaQuery()
                .eq(PortalMessageLog::getUserId, userId)
                .orderByDesc(PortalMessageLog::getCreatedAt)
                .last("limit " + safeLimit)
                .list();
    }
}
