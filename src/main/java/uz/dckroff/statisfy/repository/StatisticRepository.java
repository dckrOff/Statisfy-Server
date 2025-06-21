package uz.dckroff.statisfy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Statistic;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    Page<Statistic> findByCategory(Category category, Pageable pageable);
    List<Statistic> findByDateBetween(LocalDate startDate, LocalDate endDate);
} 