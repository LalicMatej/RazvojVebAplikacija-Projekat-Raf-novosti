package org.raflab.backendprojekat.dtos.response;

import java.util.List;

public class PageResponse<T> {

    private List<T> data;
    private int page;
    private int pageSize;
    private int totalCount;
    private int totalPages;

    public PageResponse() {}

    public PageResponse(List<T> data, int page, int pageSize, int totalCount) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
