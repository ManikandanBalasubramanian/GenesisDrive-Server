package com.test.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class FirebaseHandler {

  private static final Logger LOGGER = Logger.getLogger(FirebaseHandler.class.getName());

  public static void initializeConfig(String serviceAccountKeyPath) {
    LOGGER.info("Initializing Firebase App");
    try (FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath)) {
      final FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();
      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      LOGGER.info("Error Initializing Firebase App.\nCause: " + e.getLocalizedMessage());
    }
  }
}
