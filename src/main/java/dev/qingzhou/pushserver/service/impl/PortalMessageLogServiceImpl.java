package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.mapper.portal.PortalMessageLogMapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.service.PortalMessageLogService;
import dev.qingzhou.pushserver.model.vo.portal.PortalPageResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PortalMessageLogServiceImpl extends ServiceImpl<PortalMessageLogMapper, PortalMessageLog> implements PortalMessageLogService {

    @Override
    public List<PortalMessageLog> listRecent(Long userId, int limit, Long appId, Boolean success) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        QueryWrapper<PortalMessageLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("created_at")
                .last("limit " + safeLimit);
        if (appId != null) {
            wrapper.eq("app_id", appId);
        }
        if (success != null) {
            wrapper.eq("success", success ? 1 : 0);
        }
        return list(wrapper);
    }

    @Override
    public PortalPageResponse<PortalMessageLog> pageLogs(Long userId, Long appId, Boolean success, int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, Math.min(pageSize, 200));
        long offset = (long) (safePage - 1) * safePageSize;

        QueryWrapper<PortalMessageLog> dataWrapper = new QueryWrapper<>();
        dataWrapper.eq("user_id", userId)
                .orderByDesc("created_at")
                .last("limit " + safePageSize + " offset " + offset);
        if (appId != null) {
            dataWrapper.eq("app_id", appId);
        }
        if (success != null) {
            dataWrapper.eq("success", success ? 1 : 0);
        }
        List<PortalMessageLog> records = list(dataWrapper);

        QueryWrapper<PortalMessageLog> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId);
        if (appId != null) {
            countWrapper.eq("app_id", appId);
        }
        if (success != null) {
            countWrapper.eq("success", success ? 1 : 0);
        }
        long total = count(countWrapper);

        return PortalPageResponse.of(records, total, safePage, safePageSize);
    }
}
