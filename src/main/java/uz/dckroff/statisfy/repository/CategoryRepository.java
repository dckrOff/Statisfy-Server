package uz.dckroff.statisfy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT c.name, COUNT(f) FROM Category c LEFT JOIN Fact f ON c = f.category GROUP BY c.name ORDER BY COUNT(f) DESC")
    List<Object[]> findMostPopularCategories();
} 