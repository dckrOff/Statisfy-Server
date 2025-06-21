package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.category.CategoryRequest;
import uz.dckroff.statisfy.dto.category.CategoryResponse;
import uz.dckroff.statisfy.exception.CategoryAlreadyExistsException;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if another category with the same name exists
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException("Category with name '" + request.getName() + "' already exists");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
} 