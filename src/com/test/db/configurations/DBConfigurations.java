package com.test.db.configurations;

import com.test.config.ServerConfigurations;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Logger;

public class DBConfigurations {
  private static final Logger LOGGER = Logger.getLogger(DBConfigurations.class.getName());

  public static final String DATABASE_NAME = "com.test.db.dbname";
  public static final String SCHEMA_NAME = "com.test.db.schemaname";
  public static final String USER_NAME = "com.test.db.username";
  public static final String PASSWORD = "com.test.db.passoword";
  public static final String URL = "com.test.db.domain";
  public static final String PORT = "com.test.db.port";

  public static final String SUBSCRIPTIONS = "subscriptions";
  public static final String NFT_TABLE = "nft_contents";
  public static final String USER_TABLE = "users";
  public static final String DATA_TABLE = "contents";

  public static Properties props = new Properties();
  public static String url;

  public static void initConfiguration() {
    LOGGER.info("Initializing DBConfigurations");

    try (FileReader reader = new FileReader(ServerConfigurations.dbConfigPath)) {
      Properties properties = new Properties();
      properties.load(reader);

      url =
          properties.getProperty(URL)
              + ":"
              + properties.getProperty(PORT)
              + "/"
              + properties.getProperty(DATABASE_NAME);

      props.setProperty("user", properties.getProperty(USER_NAME));
      props.setProperty("password", properties.getProperty(PASSWORD));
      //      props.setProperty("ssl", "true");
      props.setProperty(
          "options",
          "-c search_path=" + properties.getProperty(SCHEMA_NAME) + " -c statement_timeout=900");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
