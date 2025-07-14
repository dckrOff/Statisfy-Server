package uz.dckroff.statisfy.dto.news;

import java.util.List;

public class NewsPageDto {
    private List<NewsResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    public NewsPageDto(List<NewsResponse> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public List<NewsResponse> getContent() {
        return content;
    }

    public void setContent(List<NewsResponse> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}

