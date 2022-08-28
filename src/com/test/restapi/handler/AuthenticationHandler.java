package com.test.restapi.handler;

import static com.test.config.ServerConfigurations.SESSION_COOKIE;

import com.gd.JNI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;
import com.test.db.table.UserTable;
import com.test.exception.DDException;
import com.test.firebase.FirebaseHandler;
import com.test.restapi.response.ResponseHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class AuthenticationHandler extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = Logger.getLogger(AuthenticationHandler.class.getName());

  private static JNI jni = new JNI();

  static {
    jni.getPassphrase("init");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    JSONObject json = parseJson(request);

    if (path.equals("/sessionSignup")) {
      sessionSignUp(json, request, response);
    } else if ("/getphrase".equals(path)) {
      getPhrase(json, request, response);
    } else {
      ResponseHandler.sendErrorResponse("Invalid Request", 400, response);
    }
  }

  public static String byteArrayToHex(byte[] a) {
    StringBuilder sb = new StringBuilder(a.length * 2);
    for (byte b : a) sb.append(String.format("%02x", b));
    return sb.toString();
  }

  private void getPhrase(JSONObject json, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      String uid = json.getString("uid");
      LOGGER.info("uid : " + uid);
      byte[] bArr = jni.getPassphrase(uid);

      ResponseHandler.sendSuccessResponse(
          ResponseHandler.getSuccessResponseJson(byteArrayToHex(bArr)), 200, response);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      ResponseHandler.sendErrorResponse("Invalid request", 400, response);
    }
  }

  private void sessionSignUp(
      JSONObject json, HttpServletRequest request, HttpServletResponse response) {
    try {
      LOGGER.info("SignUp");
      String idToken = json.getString("idToken");
      String username = json.getString("username");
      String passphrase = json.getString("passphrase");

      String uid = FirebaseHandler.getUID(idToken);
      String uidHash = FirebaseHandler.uidHash(uid);

      if (UserTable.getUserDetails(uidHash) != null) throw new DDException("Invalid", null);

      //      jni.getPassphrase(uid);

      UserTable ut = UserTable.addUser(username, uidHash, passphrase);

      long expiresIn = TimeUnit.DAYS.toMillis(5);
      SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

      try {
        String cookieVal = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
        Cookie cookie = new Cookie(SESSION_COOKIE, cookieVal);
        cookie.setMaxAge((int) expiresIn);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        ResponseHandler.sendSuccessResponse(
            ResponseHandler.getSuccessResponseJson("User Addition successful"), 200, response);

      } catch (FirebaseAuthException e) {
        LOGGER.severe(e.getMessage());
        ResponseHandler.sendErrorResponse("Invalid request", 400, response);
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      ResponseHandler.sendErrorResponse("Invalid request", 400, response);
    }
  }

  public static JSONObject parseJson(HttpServletRequest request) {
    StringBuilder stringBuilder = new StringBuilder();
    try (InputStream inputStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      String inputStr;
      while ((inputStr = bufferedReader.readLine()) != null) stringBuilder.append(inputStr);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return new JSONObject(stringBuilder.toString());
  }
}
