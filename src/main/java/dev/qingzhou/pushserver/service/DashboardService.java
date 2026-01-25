package dev.qingzhou.pushserver.service;

import dev.qingzhou.pushserver.model.vo.portal.DashboardChartsResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardLogResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardStatsResponse;
import java.util.List;

public interface DashboardService {

    DashboardStatsResponse fetchStats(Long userId);

    DashboardChartsResponse fetchCharts(Long userId);

    List<DashboardLogResponse> fetchRecentLogs(Long userId, int limit);
}
