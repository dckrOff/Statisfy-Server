package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.dckroff.statisfy.dto.user.UserPreferenceRequest;
import uz.dckroff.statisfy.dto.user.UserPreferenceResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.UserPreference;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.UserPreferenceRepository;
import uz.dckroff.statisfy.repository.UserRepository;
import uz.dckroff.statisfy.service.UserPreferenceService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public UserPreferenceResponse saveUserPreference(UserPreferenceRequest request, Long userId) {
        log.info("Saving preferences for user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Find or create user preference
        UserPreference userPreference = userPreferenceRepository.findByUser(user)
                .orElse(UserPreference.builder().user(user).build());
        
        // Set basic preferences
        userPreference.setInterests(request.getInterests());
        userPreference.setPreferredLanguage(request.getPreferredLanguage());
        
        // Set preferred categories
        if (request.getPreferredCategoryIds() != null && !request.getPreferredCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : request.getPreferredCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
            userPreference.setPreferredCategories(categories);
        }
        
        UserPreference savedPreference = userPreferenceRepository.save(userPreference);
        
        return mapToResponse(savedPreference);
    }

    @Override
    public UserPreferenceResponse getUserPreference(Long userId) {
        log.info("Getting preferences for user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        UserPreference userPreference = userPreferenceRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Preferences not found for user with id: " + userId));
        
        return mapToResponse(userPreference);
    }

    @Override
    @Transactional
    public void deleteUserPreference(Long userId) {
        log.info("Deleting preferences for user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        userPreferenceRepository.deleteByUser(user);
    }
    
    private UserPreferenceResponse mapToResponse(UserPreference preference) {
        return UserPreferenceResponse.builder()
                .id(preference.getId())
                .userId(preference.getUser().getId())
                .interests(preference.getInterests())
                .preferredLanguage(preference.getPreferredLanguage())
                .preferredCategoryIds(preference.getPreferredCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
} 