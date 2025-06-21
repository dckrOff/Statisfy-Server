package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.user.UserPreferenceRequest;
import uz.dckroff.statisfy.dto.user.UserPreferenceResponse;
import uz.dckroff.statisfy.model.User;

public interface UserPreferenceService {
    /**
     * Get preferences for current user
     * @return User preferences
     */
    UserPreferenceResponse getCurrentUserPreferences();
    
    /**
     * Update preferences for current user
     * @param request Preferences update request
     * @return Updated user preferences
     */
    UserPreferenceResponse updateUserPreferences(UserPreferenceRequest request);
    
    /**
     * Get preferences for a specific user
     * @param user User to get preferences for
     * @return User preferences
     */
    UserPreferenceResponse getUserPreferences(User user);
    
    /**
     * Saves or updates user preferences
     * 
     * @param request the preference data to save
     * @param userId the ID of the user
     * @return the saved user preferences
     */
    UserPreferenceResponse saveUserPreference(UserPreferenceRequest request, Long userId);
    
    /**
     * Gets the preferences for a user
     * 
     * @param userId the ID of the user
     * @return the user's preferences
     */
    UserPreferenceResponse getUserPreference(Long userId);
    
    /**
     * Deletes the preferences for a user
     * 
     * @param userId the ID of the user
     */
    void deleteUserPreference(Long userId);
} 