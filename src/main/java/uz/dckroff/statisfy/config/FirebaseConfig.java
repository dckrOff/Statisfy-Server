package uz.dckroff.statisfy.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.config-file}")
    private String firebaseConfigFile;

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        if (!firebaseEnabled) {
            log.warn("Firebase is disabled. Using mock implementation.");
            return null;
        }

        try {
            InputStream serviceAccount = new ClassPathResource(firebaseConfigFile).getInputStream();
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options, "statisfy");
                log.info("Firebase application has been initialized with name: {}", app.getName());
            }

            return FirebaseMessaging.getInstance();
        } catch (IOException e) {
            log.error("Error initializing Firebase: {}", e.getMessage());
            throw e;
        }
    }
} 