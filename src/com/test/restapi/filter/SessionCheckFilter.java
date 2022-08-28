package com.test.restapi.filter;

import com.test.config.ServerConfigurations;
import com.test.db.configurations.DBConfigurations;
import com.test.firebase.FirebaseHandler;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionCheckFilter implements Filter {
  private static final Logger LOGGER = Logger.getLogger(SessionCheckFilter.class.getName());

  @Override
  public void init(FilterConfig fConfig) throws ServletException {
    ServerConfigurations.initalize(fConfig.getServletContext().getRealPath("/WEB-INF"));
    DBConfigurations.initConfiguration();
    FirebaseHandler.initializeConfig(ServerConfigurations.serviceAccountKeyPath);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    LOGGER.info("Init Haders");
    ((HttpServletResponse) response)
        .setHeader(
            "Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
    ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
    ((HttpServletResponse) response)
        .setHeader("Access-Control-Allow-Methods", "*,HEAD, POST, GET, OPTIONS, DELETE");
    ((HttpServletResponse) response).setHeader("Access-Control-Max-Age", "3600");
    ((HttpServletResponse) response)
        .setHeader(
            "Access-Control-Allow-Headers",
            "*,Content-Type, Accept, X-Requested-With, remember-me");

    chain.doFilter(request, response);
  }
}
