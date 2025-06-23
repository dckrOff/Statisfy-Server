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
            log.warn("Firebase is disabled. Skipping FirebaseMessaging bean.");
            return null;
        }

        InputStream serviceAccount = new ClassPathResource(firebaseConfigFile).getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp app;
        if (FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.initializeApp(options, "statisfy"); // создаёшь с ИМЕНЕМ
            log.info("Firebase application initialized: {}", app.getName());
        } else {
            // получаем по имени, чтобы не получить исключение
            app = FirebaseApp.getInstance("statisfy");
            log.info("Firebase application already exists: {}", app.getName());
        }

        return FirebaseMessaging.getInstance(app); // ← ВАЖНО: передаём именно этот app
    }


}