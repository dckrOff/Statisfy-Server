package uz.dckroff.statisfy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.News;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByIsRelevantTrue(Pageable pageable);
    Page<News> findByCategory(Category category, Pageable pageable);
    Page<News> findByCategoryAndIsRelevantTrue(Category category, Pageable pageable);
    List<News> findByPublishedAtAfterOrderByPublishedAtDesc(LocalDateTime date);
    boolean existsByTitleAndUrl(String title, String url);
} 