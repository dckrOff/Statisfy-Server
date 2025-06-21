package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.category.CategoryRequest;
import uz.dckroff.statisfy.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
} 