package com.test.restapi.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.test.config.ServerConfigurations;
import com.test.db.configurations.DBConfigurations;
import com.test.db.table.UserTable;
import com.test.exception.DDException;
import com.test.firebase.FirebaseHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionCheckFilter implements Filter {
  private static final Logger LOGGER = Logger.getLogger(SessionCheckFilter.class.getName());
  private String[] skipUrls;
  private Predicate<String> isSkipUrl;

  @Override
  public void init(FilterConfig fConfig) throws ServletException {

    ServerConfigurations.initalize(fConfig.getServletContext().getRealPath("/WEB-INF"));
    DBConfigurations.initConfiguration();
    FirebaseHandler.initializeConfig(ServerConfigurations.serviceAccountKeyPath);

    skipUrls = fConfig.getInitParameter("skip").split(","); // No I18N
    isSkipUrl = (url) -> Arrays.stream(skipUrls).anyMatch(skipUrl -> url.contains(skipUrl));

    LOGGER.info("App initialization successful");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String path = ((HttpServletRequest) request).getRequestURI();
    boolean isSkip = isSkipUrl.test(path);
    boolean isValidCookie = validateCookie((HttpServletRequest) request);

    if (isSkip && isValidCookie) {
      ((HttpServletResponse) response).sendRedirect("/");
    } else if (!isSkip && !isValidCookie) {
      ((HttpServletResponse) response).sendRedirect("/signin");
      //    } else if (!isSkip && isValidCookie) {
      //      if (isUserConfigured((HttpServletRequest) request)) chain.doFilter(request, response);
      //      else ((HttpServletResponse) response).sendRedirect("/configuration.html");
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean validateCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    String cookieVal = ServerConfigurations.getSessionCookie(cookies);

    if (cookieVal == null) return false;

    try {
      final boolean checkRevoked = true;
      FirebaseToken decodedToken =
          FirebaseAuth.getInstance().verifySessionCookie(cookieVal, checkRevoked);
      request.setAttribute("uid", decodedToken.getUid());
    } catch (FirebaseAuthException e) {
      return false;
    }

    return true;
  }

  private boolean isUserConfigured(HttpServletRequest request) {
    try {
      String uid = request.getParameter("uid");
      return UserTable.isUserConfigured(uid);
    } catch (DDException e) {
      return false;
    }
  }
}
