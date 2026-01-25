package dev.qingzhou.pushserver.model.vo.portal;

public class DashboardStatsResponse {

    private long todayTotal;
    private double successRate;
    private long activeApps;
    private String lastErrorTime;

    public long getTodayTotal() {
        return todayTotal;
    }

    public void setTodayTotal(long todayTotal) {
        this.todayTotal = todayTotal;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public long getActiveApps() {
        return activeApps;
    }

    public void setActiveApps(long activeApps) {
        this.activeApps = activeApps;
    }

    public String getLastErrorTime() {
        return lastErrorTime;
    }

    public void setLastErrorTime(String lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
    }
}
