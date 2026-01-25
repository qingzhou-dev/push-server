package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import dev.qingzhou.pushserver.common.PortalSessionSupport;
import dev.qingzhou.pushserver.model.vo.portal.DashboardChartsResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardLogResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardStatsResponse;
import dev.qingzhou.pushserver.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public PortalResponse<DashboardStatsResponse> stats(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        return PortalResponse.ok(dashboardService.fetchStats(userId));
    }

    @GetMapping("/charts")
    public PortalResponse<DashboardChartsResponse> charts(HttpSession session) {
        Long userId = PortalSessionSupport.requireUserId(session);
        return PortalResponse.ok(dashboardService.fetchCharts(userId));
    }

    @GetMapping("/recent-logs")
    public PortalResponse<List<DashboardLogResponse>> recentLogs(
            @RequestParam(defaultValue = "10") int limit,
            HttpSession session
    ) {
        Long userId = PortalSessionSupport.requireUserId(session);
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return PortalResponse.ok(dashboardService.fetchRecentLogs(userId, safeLimit));
    }
}
