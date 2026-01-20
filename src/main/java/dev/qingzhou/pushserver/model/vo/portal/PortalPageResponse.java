package dev.qingzhou.pushserver.model.vo.portal;

import java.util.List;

public class PortalPageResponse<T> {

    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public PortalPageResponse() {
    }

    public PortalPageResponse(List<T> records, long total, int page, int pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static <T> PortalPageResponse<T> of(List<T> records, long total, int page, int pageSize) {
        return new PortalPageResponse<>(records, total, page, pageSize);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
