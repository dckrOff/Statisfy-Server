package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.newsapi.NewsApiResponse;

public interface NewsApiService {
    NewsApiResponse fetchTopHeadlines(String category, String country);
    NewsApiResponse searchNews(String query, String sortBy, int pageSize);
} 