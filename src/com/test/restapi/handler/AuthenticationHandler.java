package com.test.restapi.handler;

import static com.test.config.ServerConfigurations.SESSION_COOKIE;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;
import com.test.config.ServerConfigurations;
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

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    LOGGER.info("SignOut");
    String cookieVal = ServerConfigurations.getSessionCookie(request.getCookies());
    Cookie cookie = new Cookie(SESSION_COOKIE, cookieVal);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    ((HttpServletResponse) response).sendRedirect("/signin");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();

    if (path.equals("/sessionLogin")) {
      sessionLogin(request, response);
    } else if (path.equals("/sessionSignup")) {
      sessionSignUp(request, response);
    }
  }

  private void sessionSignUp(HttpServletRequest request, HttpServletResponse response) {
    LOGGER.info("SignUp");
    JSONObject output = new JSONObject();
    long expiresIn = TimeUnit.DAYS.toMillis(5);
    JSONObject json = parseJson(request);
    //    String username = json.getString("username");
    String username = "temp";
    String idToken = json.getString("idToken");

    SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

    try {
      String uid = FirebaseHandler.getUID(idToken);

      String cookieVal = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
      Cookie cookie = new Cookie(SESSION_COOKIE, cookieVal);
      cookie.setMaxAge((int) expiresIn);
      cookie.setHttpOnly(true);
      UserTable ut = UserTable.addUser(username, uid);

      response.addCookie(cookie);
      output = ResponseHandler.getSuccessResponseJson(ut.toJson());
    } catch (FirebaseAuthException | DDException e) {
      output = ResponseHandler.getErrorResponseJson("Error Authenticating user");
    }
    ResponseHandler.writeResponse(output, response);
  }

  private void sessionLogin(HttpServletRequest request, HttpServletResponse response) {
    LOGGER.info("SignIn");
    JSONObject output = new JSONObject();
    long expiresIn = TimeUnit.DAYS.toMillis(5);
    String idToken = parseJson(request).getString("idToken");
    SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

    try {
      String cookieVal = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
      Cookie cookie = new Cookie(SESSION_COOKIE, cookieVal);
      cookie.setMaxAge((int) expiresIn);
      cookie.setHttpOnly(true);
      response.addCookie(cookie);
      output = ResponseHandler.getSuccessResponseJson("");
    } catch (FirebaseAuthException e) {
      output = ResponseHandler.getErrorResponseJson("Error Authenticating user");
    }
    ResponseHandler.writeResponse(output, response);
  }

  private JSONObject parseJson(HttpServletRequest request) {
    StringBuilder stringBuilder = new StringBuilder();
    try (InputStream inputStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      char[] charBuffer = new char[128];
      int bytesRead = -1;
      while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
        stringBuilder.append(charBuffer, 0, bytesRead);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return new JSONObject(stringBuilder.toString());
  }
}
