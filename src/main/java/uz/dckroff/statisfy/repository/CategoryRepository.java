package uz.dckroff.statisfy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
} 