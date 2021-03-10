package com.skillsfighters.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.skillsfighters.runnable.Main;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FirebaseUtils {
    public static void checkAndInitializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                final InputStream serviceAccount = Main.class.getResourceAsStream("/firebase_credentials.json");
                final FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://activity-tracker-42a4e.firebaseio.com/").build();
                FirebaseApp.initializeApp(options);
            } catch (IOException | NullPointerException exception) {
                log.error("File with firebase credentials not found");
                throw new IllegalStateException("File with firebase credentials not found");
            }
        }
    }
}
