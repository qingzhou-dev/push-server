package dev.qingzhou.pushserver.model.vo.portal;

import java.util.List;

public class DashboardChartsResponse {

    private List<TrendPoint> trend;
    private List<DistributionSlice> distribution;

    public List<TrendPoint> getTrend() {
        return trend;
    }

    public void setTrend(List<TrendPoint> trend) {
        this.trend = trend;
    }

    public List<DistributionSlice> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<DistributionSlice> distribution) {
        this.distribution = distribution;
    }

    public static class TrendPoint {
        private String date;
        private long count;

        public TrendPoint() {
        }

        public TrendPoint(String date, long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class DistributionSlice {
        private String name;
        private long value;

        public DistributionSlice() {
        }

        public DistributionSlice(String name, long value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }
}
