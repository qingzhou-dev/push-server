package dev.qingzhou.pushserver.v2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.qingzhou.pushserver.v2.mapper.V2MessageLogMapper;
import dev.qingzhou.pushserver.v2.model.V2MessageLog;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class V2MessageLogService extends ServiceImpl<V2MessageLogMapper, V2MessageLog> {

    public List<V2MessageLog> listRecent(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return lambdaQuery()
                .eq(V2MessageLog::getUserId, userId)
                .orderByDesc(V2MessageLog::getCreatedAt)
                .last("limit " + safeLimit)
                .list();
    }
}
