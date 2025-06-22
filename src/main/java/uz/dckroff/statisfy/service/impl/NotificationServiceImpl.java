package uz.dckroff.statisfy.service.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.dto.notification.DeviceRegistrationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsRequest;
import uz.dckroff.statisfy.dto.notification.NotificationSettingsResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.DeviceToken;
import uz.dckroff.statisfy.model.NotificationSettings;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.repository.DeviceTokenRepository;
import uz.dckroff.statisfy.repository.NotificationSettingsRepository;
import uz.dckroff.statisfy.repository.UserRepository;
import uz.dckroff.statisfy.service.AIService;
import uz.dckroff.statisfy.service.NewsService;
import uz.dckroff.statisfy.service.NotificationService;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final AIService aiService;
    private final NewsService newsService;
    
    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Override
    @Transactional
    public boolean registerDevice(DeviceRegistrationRequest request, User user) {
        try {
            log.info("Registering device for user: {}", user.getUsername());
            
            // Check if token already exists
            Optional<DeviceToken> existingToken = deviceTokenRepository.findByToken(request.getToken());
            
            if (existingToken.isPresent()) {
                // Update existing token
                DeviceToken token = existingToken.get();
                token.setUser(user);
                token.setDeviceType(request.getDeviceType());
                token.setActive(true);
                token.setLastUsedAt(java.time.LocalDateTime.now());
                deviceTokenRepository.save(token);
                return true;
            }
            
            // Create new token
            DeviceToken deviceToken = DeviceToken.builder()
                    .token(request.getToken())
                    .user(user)
                    .deviceType(request.getDeviceType())
                    .build();
            
            deviceTokenRepository.save(deviceToken);
            return true;
        } catch (Exception e) {
            log.error("Error registering device: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unregisterDevice(String token) {
        try {
            log.info("Unregistering device token: {}", token);
            
            if (!deviceTokenRepository.existsByToken(token)) {
                return false;
            }
            
            deviceTokenRepository.deleteByToken(token);
            return true;
        } catch (Exception e) {
            log.error("Error unregistering device: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int sendNotification(NotificationRequest request) {
        try {
            log.info("Sending notification: {}", request.getTitle());
            
            if (!firebaseEnabled) {
                log.warn("Firebase is disabled. Notification not sent: {}", request.getTitle());
                return 0;
            }
            
            List<String> tokens = new ArrayList<>();
            
            if (request.isSendToAll()) {
                // Send to all users
                List<DeviceToken> allTokens = deviceTokenRepository.findAll().stream()
                        .filter(DeviceToken::isActive)
                        .collect(Collectors.toList());
                
                tokens = allTokens.stream()
                        .map(DeviceToken::getToken)
                        .collect(Collectors.toList());
            } else if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                // Send to specific users
                for (Long userId : request.getUserIds()) {
                    List<DeviceToken> userTokens = deviceTokenRepository.findActiveTokensByUserId(userId);
                    tokens.addAll(userTokens.stream()
                            .map(DeviceToken::getToken)
                            .collect(Collectors.toList()));
                }
            } else if (request.getTopic() != null && !request.getTopic().isEmpty()) {
                // Send to topic
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImageUrl())
                                .build())
                        .putAllData(request.getData() != null ? request.getData() : Collections.emptyMap())
                        .setTopic(request.getTopic())
                        .build();
                
                firebaseMessaging.send(message);
                return 1;
            }
            
            if (tokens.isEmpty()) {
                log.warn("No device tokens found for notification: {}", request.getTitle());
                return 0;
            }
            
            // Send to multiple devices
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody())
                            .setImage(request.getImageUrl())
                            .build())
                    .putAllData(request.getData() != null ? request.getData() : Collections.emptyMap())
                    .addAllTokens(tokens)
                    .build();
            
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("Sent notification to {} devices, success: {}, failure: {}", 
                    tokens.size(), response.getSuccessCount(), response.getFailureCount());
            
            return response.getSuccessCount();
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean sendNotificationToUser(Long userId, String title, String body, Map<String, String> data) {
        try {
            log.info("Sending notification to user ID {}: {}", userId, title);
            
            if (!firebaseEnabled) {
                log.warn("Firebase is disabled. Notification not sent to user {}: {}", userId, title);
                return false;
            }
            
            List<DeviceToken> tokens = deviceTokenRepository.findActiveTokensByUserId(userId);
            
            if (tokens.isEmpty()) {
                log.warn("No device tokens found for user ID: {}", userId);
                return false;
            }
            
            List<String> tokenStrings = tokens.stream()
                    .map(DeviceToken::getToken)
                    .collect(Collectors.toList());
            
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data != null ? data : Collections.emptyMap())
                    .addAllTokens(tokenStrings)
                    .build();
            
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("Sent notification to user {}, success: {}, failure: {}", 
                    userId, response.getSuccessCount(), response.getFailureCount());
            
            return response.getSuccessCount() > 0;
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public int sendNotificationToAll(String title, String body, Map<String, String> data) {
        NotificationRequest request = NotificationRequest.builder()
                .title(title)
                .body(body)
                .data(data)
                .sendToAll(true)
                .build();
        
        return sendNotification(request);
    }

    @Override
    public NotificationSettingsResponse getNotificationSettings(User user) {
        log.info("Getting notification settings for user: {}", user.getUsername());
        
        NotificationSettings settings = notificationSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        return mapToResponse(settings);
    }

    @Override
    @Transactional
    public NotificationSettingsResponse updateNotificationSettings(NotificationSettingsRequest request, User user) {
        log.info("Updating notification settings for user: {}", user.getUsername());
        
        NotificationSettings settings = notificationSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        
        // Update settings only if values are provided
        if (request.getDailyFactEnabled() != null) {
            settings.setDailyFactEnabled(request.getDailyFactEnabled());
        }
        
        if (request.getWeeklyNewsEnabled() != null) {
            settings.setWeeklyNewsEnabled(request.getWeeklyNewsEnabled());
        }
        
        if (request.getBreakingNewsEnabled() != null) {
            settings.setBreakingNewsEnabled(request.getBreakingNewsEnabled());
        }
        
        if (request.getPreferredTime() != null) {
            settings.setPreferredTime(request.getPreferredTime());
        }
        
        if (request.getTimezone() != null) {
            settings.setTimezone(request.getTimezone());
        }
        
        if (request.getQuietHoursStart() != null) {
            settings.setQuietHoursStart(request.getQuietHoursStart());
        }
        
        if (request.getQuietHoursEnd() != null) {
            settings.setQuietHoursEnd(request.getQuietHoursEnd());
        }
        
        if (request.getQuietHoursEnabled() != null) {
            settings.setQuietHoursEnabled(request.getQuietHoursEnabled());
        }
        
        NotificationSettings savedSettings = notificationSettingsRepository.save(settings);
        
        return mapToResponse(savedSettings);
    }

    @Override
    public void sendDailyFactNotifications() {
        log.info("Sending daily fact notifications");
        
        if (!firebaseEnabled) {
            log.warn("Firebase is disabled. Daily fact notifications not sent.");
            return;
        }
        
        LocalTime currentTime = LocalTime.now();
        // Round to the nearest hour to match with preferred times
        LocalTime roundedTime = LocalTime.of(currentTime.getHour(), 0);
        
        List<NotificationSettings> eligibleSettings = notificationSettingsRepository
                .findAllForDailyFactAtTime(roundedTime);
        
        log.info("Found {} users eligible for daily fact notification at {}", eligibleSettings.size(), roundedTime);
        
        for (NotificationSettings settings : eligibleSettings) {
            try {
                User user = settings.getUser();
                
                // Generate personalized fact
                FactResponse fact = aiService.generateDailyFact(user);
                
                // Prepare notification data
                Map<String, String> data = new HashMap<>();
                data.put("factId", fact.getId().toString());
                data.put("type", "daily_fact");
                
                // Send notification
                sendNotificationToUser(
                        user.getId(),
                        "Daily Fact: " + fact.getTitle(),
                        fact.getContent(),
                        data
                );
                
                log.info("Sent daily fact notification to user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Error sending daily fact notification to user ID {}: {}", 
                        settings.getUser().getId(), e.getMessage());
            }
        }
    }

    @Override
    public void sendWeeklyNewsSummary() {
        log.info("Sending weekly news summary notifications");
        
        if (!firebaseEnabled) {
            log.warn("Firebase is disabled. Weekly news summary notifications not sent.");
            return;
        }
        
        List<NotificationSettings> eligibleSettings = notificationSettingsRepository.findAllForWeeklyNews();
        
        log.info("Found {} users eligible for weekly news summary", eligibleSettings.size());
        
        for (NotificationSettings settings : eligibleSettings) {
            try {
                User user = settings.getUser();
                
                // Get relevant news for user (simplified, in real implementation would use user preferences)
                var relevantNews = newsService.getRelevantNews(0, 5).getContent();
                String newsCount = String.valueOf(relevantNews.size());
                
                // Prepare notification data
                Map<String, String> data = new HashMap<>();
                data.put("type", "weekly_news");
                data.put("newsCount", newsCount);
                
                // Send notification
                sendNotificationToUser(
                        user.getId(),
                        "Your Weekly News Summary",
                        "We've found " + newsCount + " interesting articles for you this week.",
                        data
                );
                
                log.info("Sent weekly news summary notification to user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Error sending weekly news summary to user ID {}: {}", 
                        settings.getUser().getId(), e.getMessage());
            }
        }
    }
    
    private NotificationSettings createDefaultSettings(User user) {
        NotificationSettings settings = NotificationSettings.builder()
                .user(user)
                .dailyFactEnabled(true)
                .weeklyNewsEnabled(true)
                .breakingNewsEnabled(true)
                .preferredTime(LocalTime.of(9, 0)) // Default to 9:00 AM
                .timezone("UTC")
                .quietHoursEnabled(false)
                .build();
        
        return notificationSettingsRepository.save(settings);
    }
    
    private NotificationSettingsResponse mapToResponse(NotificationSettings settings) {
        return NotificationSettingsResponse.builder()
                .id(settings.getId())
                .userId(settings.getUser().getId())
                .dailyFactEnabled(settings.isDailyFactEnabled())
                .weeklyNewsEnabled(settings.isWeeklyNewsEnabled())
                .breakingNewsEnabled(settings.isBreakingNewsEnabled())
                .preferredTime(settings.getPreferredTime())
                .timezone(settings.getTimezone())
                .quietHoursStart(settings.getQuietHoursStart())
                .quietHoursEnd(settings.getQuietHoursEnd())
                .quietHoursEnabled(settings.isQuietHoursEnabled())
                .build();
    }
} 