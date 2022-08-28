package com.test.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.test.exception.DDException;
import com.test.exception.DDException.StatusCode;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
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

  public static String uidHash(String uid) throws DDException {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] hash = digest.digest(uid.getBytes("UTF-8"));
      final StringBuilder hexString = new StringBuilder();
      for (int i = 0; i < hash.length; i++) {
        final String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception ex) {
      throw new DDException(StatusCode.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
    }
  }

  public static String getUID(String idToken) throws FirebaseAuthException {
    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
    return decodedToken.getUid();
  }

  public static String getUIDFromCookie(String sessionCookie) throws FirebaseAuthException {
    final boolean checkRevoked = true;
    FirebaseToken decodedToken =
        FirebaseAuth.getInstance().verifySessionCookie(sessionCookie, checkRevoked);
    return decodedToken.getUid();
  }
}
