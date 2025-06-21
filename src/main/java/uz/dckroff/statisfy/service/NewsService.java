package uz.dckroff.statisfy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.dckroff.statisfy.dto.news.NewsResponse;

import java.util.List;

public interface NewsService {
    Page<NewsResponse> getAllNews(Pageable pageable);
    Page<NewsResponse> getNewsByCategory(Long categoryId, Pageable pageable);
    Page<NewsResponse> getRelevantNews(Pageable pageable);
    NewsResponse getNewsById(Long id);
    void fetchNewsFromExternalApi();
    List<NewsResponse> getLatestNews();
} 