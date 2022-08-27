package com.test.config;

import javax.servlet.http.Cookie;

public class ServerConfigurations {

  public static String appHomePath;
  public static String resourcesPath;
  public static String serviceAccountKeyPath;
  public static String confDirPath;
  public static String dbConfigPath;
  public static final String DB_CONFIGURATION = "DBConfigurations.properties";
  public static final String SESSION_COOKIE = "session";

  public static void initalize(String appHome) {
    appHomePath = appHome;
    resourcesPath = appHomePath + "/resources";
    confDirPath = appHomePath + "/conf";
    dbConfigPath = confDirPath + "/" + DB_CONFIGURATION;
    serviceAccountKeyPath = resourcesPath + "/secret/secretKey.json";
  }

  public static String getSessionCookie(Cookie[] cookies) {
    if (cookies == null) return null;
    for (Cookie c : cookies) {
      if (SESSION_COOKIE.equals(c.getName())) return c.getValue();
    }
    return null;
  }
}
