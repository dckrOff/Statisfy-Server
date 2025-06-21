package uz.dckroff.statisfy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Fact;

import java.util.List;

@Repository
public interface FactRepository extends JpaRepository<Fact, Long> {
    Page<Fact> findByIsPublishedTrue(Pageable pageable);
    Page<Fact> findByCategoryAndIsPublishedTrue(Category category, Pageable pageable);
    List<Fact> findTop5ByIsPublishedTrueOrderByCreatedAtDesc();
} 