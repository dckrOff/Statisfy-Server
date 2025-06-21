package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.dckroff.statisfy.dto.newsapi.NewsApiResponse;
import uz.dckroff.statisfy.service.NewsApiService;

@Service
@RequiredArgsConstructor
public class NewsApiServiceImpl implements NewsApiService {

    private final RestTemplate restTemplate;

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${newsapi.baseurl}")
    private String baseUrl;

    @Override
    public NewsApiResponse fetchTopHeadlines(String category, String country) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/top-headlines")
                .queryParam("apiKey", apiKey)
                .queryParam("country", country)
                .queryParam("category", category)
                .build()
                .toUriString();

        return restTemplate.getForObject(url, NewsApiResponse.class);
    }

    @Override
    public NewsApiResponse searchNews(String query, String sortBy, int pageSize) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/everything")
                .queryParam("apiKey", apiKey)
                .queryParam("q", query)
                .queryParam("sortBy", sortBy)
                .queryParam("pageSize", pageSize)
                .build()
                .toUriString();

        return restTemplate.getForObject(url, NewsApiResponse.class);
    }
} 