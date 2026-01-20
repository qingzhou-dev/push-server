package dev.qingzhou.pushserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.qingzhou.pushserver.model.entity.portal.PortalMessageLog;
import dev.qingzhou.pushserver.model.entity.portal.PortalWecomApp;
import dev.qingzhou.pushserver.model.vo.portal.DashboardChartsResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardLogResponse;
import dev.qingzhou.pushserver.model.vo.portal.DashboardStatsResponse;
import dev.qingzhou.pushserver.service.DashboardService;
import dev.qingzhou.pushserver.service.PortalMessageLogService;
import dev.qingzhou.pushserver.service.PortalWecomAppService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final PortalMessageLogService messageLogService;
    private final PortalWecomAppService appService;

    public DashboardServiceImpl(PortalMessageLogService messageLogService, PortalWecomAppService appService) {
        this.messageLogService = messageLogService;
        this.appService = appService;
    }

    @Override
    public DashboardStatsResponse fetchStats(Long userId) {
        long startOfDay = atStartOfDayMillis(0);

        long todayTotal = countLogs(userId, startOfDay, null);
        long todaySuccess = countLogs(userId, startOfDay, 1);
        double successRate = todayTotal == 0 ? 0.0 : todaySuccess * 100.0 / todayTotal;

        long activeApps = appService.lambdaQuery()
                .eq(PortalWecomApp::getUserId, userId)
                .count();

        PortalMessageLog lastError = messageLogService.getOne(new QueryWrapper<PortalMessageLog>()
                .eq("user_id", userId)
                .ge("created_at", startOfDay)
                .eq("success", 0)
                .orderByDesc("created_at")
                .last("limit 1"), false);
        String lastErrorTime = null;
        if (lastError != null && lastError.getCreatedAt() != null) {
            lastErrorTime = Instant.ofEpochMilli(lastError.getCreatedAt())
                    .atZone(ZONE)
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        DashboardStatsResponse response = new DashboardStatsResponse();
        response.setTodayTotal(todayTotal);
        response.setSuccessRate(successRate);
        response.setActiveApps(activeApps);
        response.setLastErrorTime(lastErrorTime);
        return response;
    }

    @Override
    public DashboardChartsResponse fetchCharts(Long userId) {
        Map<Long, String> appNames = loadAppNames(userId);

        DashboardChartsResponse response = new DashboardChartsResponse();
        response.setTrend(buildTrend(userId));
        response.setDistribution(buildDistribution(userId, appNames));
        return response;
    }

    @Override
    public List<DashboardLogResponse> fetchRecentLogs(Long userId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        Map<Long, String> appNames = loadAppNames(userId);

        List<PortalMessageLog> logs = messageLogService.list(new QueryWrapper<PortalMessageLog>()
                .eq("user_id", userId)
                .orderByDesc("created_at")
                .last("limit " + safeLimit));

        List<DashboardLogResponse> responses = new ArrayList<>(logs.size());
        for (PortalMessageLog log : logs) {
            DashboardLogResponse item = new DashboardLogResponse();
            item.setTime(formatDateTime(log.getCreatedAt()));
            item.setAppName(resolveAppName(appNames, log.getAppId(), log.getAgentId()));
            item.setReceiver(resolveReceiver(log));
            item.setStatus(log.getSuccess() != null && log.getSuccess() == 1 ? 1 : 0);
            item.setErrorMsg(log.getErrorMessage());
            responses.add(item);
        }
        return responses;
    }

    private long countLogs(Long userId, long startTime, Integer success) {
        QueryWrapper<PortalMessageLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .ge("created_at", startTime);
        if (success != null) {
            wrapper.eq("success", success);
        }
        return messageLogService.count(wrapper);
    }

    private List<DashboardChartsResponse.TrendPoint> buildTrend(Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate startDate = today.minusDays(6);
        long startMillis = startDate.atStartOfDay(ZONE).toInstant().toEpochMilli();

        Map<LocalDate, Long> counts = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            counts.put(date, 0L);
        }

        List<PortalMessageLog> logs = messageLogService.list(new QueryWrapper<PortalMessageLog>()
                .eq("user_id", userId)
                .ge("created_at", startMillis));
        for (PortalMessageLog log : logs) {
            if (log.getCreatedAt() == null) {
                continue;
            }
            LocalDate date = Instant.ofEpochMilli(log.getCreatedAt()).atZone(ZONE).toLocalDate();
            if (!date.isBefore(startDate) && !date.isAfter(today)) {
                counts.put(date, counts.getOrDefault(date, 0L) + 1);
            }
        }

        List<DashboardChartsResponse.TrendPoint> trend = new ArrayList<>(counts.size());
        for (Map.Entry<LocalDate, Long> entry : counts.entrySet()) {
            trend.add(new DashboardChartsResponse.TrendPoint(entry.getKey().format(DATE_FMT), entry.getValue()));
        }
        return trend;
    }

    private List<DashboardChartsResponse.DistributionSlice> buildDistribution(Long userId, Map<Long, String> appNames) {
        long startMillis = atStartOfDayMillis(29);
        List<PortalMessageLog> logs = messageLogService.list(new QueryWrapper<PortalMessageLog>()
                .eq("user_id", userId)
                .ge("created_at", startMillis));
        Map<Long, Long> counts = new HashMap<>();
        for (PortalMessageLog log : logs) {
            if (log.getAppId() == null) {
                continue;
            }
            counts.put(log.getAppId(), counts.getOrDefault(log.getAppId(), 0L) + 1);
        }

        List<DashboardChartsResponse.DistributionSlice> slices = counts.entrySet().stream()
                .map(e -> new DashboardChartsResponse.DistributionSlice(
                        resolveAppName(appNames, e.getKey(), null),
                        e.getValue()))
                .sorted(Comparator.comparingLong(DashboardChartsResponse.DistributionSlice::getValue).reversed())
                .collect(Collectors.toList());

        if (slices.size() > 5) {
            List<DashboardChartsResponse.DistributionSlice> top = new ArrayList<>(slices.subList(0, 5));
            long other = slices.subList(5, slices.size()).stream()
                    .mapToLong(DashboardChartsResponse.DistributionSlice::getValue)
                    .sum();
            top.add(new DashboardChartsResponse.DistributionSlice("Other", other));
            slices = top;
        }

        return slices;
    }

    private Map<Long, String> loadAppNames(Long userId) {
        List<PortalWecomApp> apps = appService.lambdaQuery()
                .eq(PortalWecomApp::getUserId, userId)
                .list();
        if (apps == null || apps.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> map = new HashMap<>();
        for (PortalWecomApp app : apps) {
            String name = app.getName();
            if (name == null || name.isBlank()) {
                name = app.getAgentId();
            }
            map.put(app.getId(), name);
        }
        return map;
    }

    private String resolveAppName(Map<Long, String> appNames, Long appId, String agentId) {
        if (appId != null && appNames.containsKey(appId)) {
            return appNames.get(appId);
        }
        if (agentId != null) {
            return agentId;
        }
        return "Unknown app";
    }

    private String resolveReceiver(PortalMessageLog log) {
        if (log.getToAll() != null && log.getToAll() == 1) {
            return "ALL";
        }
        List<String> parts = new ArrayList<>(2);
        if (log.getToUser() != null && !log.getToUser().isBlank()) {
            parts.add(log.getToUser());
        }
        if (log.getToParty() != null && !log.getToParty().isBlank()) {
            parts.add(log.getToParty());
        }
        if (parts.isEmpty()) {
            return "--";
        }
        return String.join(" / ", parts);
    }

    private String formatDateTime(Long millis) {
        if (millis == null) {
            return null;
        }
        return Instant.ofEpochMilli(millis).atZone(ZONE).toLocalDateTime().format(DATETIME_FMT);
    }

    private long atStartOfDayMillis(int daysAgo) {
        return LocalDate.now(ZONE)
                .minusDays(daysAgo)
                .atStartOfDay(ZONE)
                .toInstant()
                .toEpochMilli();
    }
}
