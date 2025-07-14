package uz.dckroff.statisfy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.PagedResponse;
import uz.dckroff.statisfy.dto.news.NewsResponse;
import uz.dckroff.statisfy.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public PagedResponse<NewsResponse> getAllNews(Pageable pageable) {
        return newsService.getAllNews(pageable);
    }



    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<NewsResponse>> getNewsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(newsService.getNewsByCategory(categoryId, pageable));
    }

    @GetMapping("/relevant")
    public ResponseEntity<Page<NewsResponse>> getRelevantNews(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(newsService.getRelevantNews(pageable));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponse>> getLatestNews() {
        return ResponseEntity.ok(newsService.getLatestNews());
    }

    @PostMapping("/fetch")
    public ResponseEntity<Void> fetchNewsFromExternalApi() {
        newsService.fetchNewsFromExternalApi();
        return ResponseEntity.ok().build();
    }
} 