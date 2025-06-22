package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.notification.DeviceRegistrationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsResponse;
import uz.dckroff.statisfy.model.User;

import java.util.Map;

public interface NotificationService {

    /**
     * Registers a device token for the current user
     * 
     * @param request the device registration request
     * @param user the user to register the device for
     * @return true if registration was successful
     */
    boolean registerDevice(DeviceRegistrationRequest request, User user);
    
    /**
     * Unregisters a device token
     * 
     * @param token the device token to unregister
     * @return true if unregistration was successful
     */
    boolean unregisterDevice(String token);
    
    /**
     * Sends a notification to users
     * 
     * @param request the notification request
     * @return the number of notifications sent
     */
    int sendNotification(NotificationRequest request);
    
    /**
     * Sends a notification to a specific user
     * 
     * @param userId the ID of the user to send the notification to
     * @param title the notification title
     * @param body the notification body
     * @param data additional data to send with the notification
     * @return true if the notification was sent successfully
     */
    boolean sendNotificationToUser(Long userId, String title, String body, Map<String, String> data);
    
    /**
     * Sends a notification to all users
     * 
     * @param title the notification title
     * @param body the notification body
     * @param data additional data to send with the notification
     * @return the number of notifications sent
     */
    int sendNotificationToAll(String title, String body, Map<String, String> data);
    
    /**
     * Gets the notification settings for a user
     * 
     * @param user the user to get settings for
     * @return the notification settings
     */
    NotificationSettingsResponse getNotificationSettings(User user);
    
    /**
     * Updates the notification settings for a user
     * 
     * @param request the notification settings request
     * @param user the user to update settings for
     * @return the updated notification settings
     */
    NotificationSettingsResponse updateNotificationSettings(NotificationSettingsRequest request, User user);
    
    /**
     * Sends the daily fact notification to users
     */
    void sendDailyFactNotifications();
    
    /**
     * Sends the weekly news summary notification to users
     */
    void sendWeeklyNewsSummary();
} 