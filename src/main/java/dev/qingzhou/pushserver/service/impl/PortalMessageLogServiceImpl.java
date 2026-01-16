package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        QueryWrapper<PortalMessageLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("created_at")
                .last("limit " + safeLimit);
        return list(wrapper);
    }
}
