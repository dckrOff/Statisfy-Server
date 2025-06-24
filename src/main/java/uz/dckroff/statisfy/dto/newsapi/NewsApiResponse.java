package uz.dckroff.statisfy.dto.newsapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsApiResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        private Source source;
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonProperty("publishedAt")
        private String publishedAt;
        
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {
        private String id;
        private String name;
    }
} 