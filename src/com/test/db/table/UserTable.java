package com.test.db.table;

import com.test.db.utils.DBUtils;
import com.test.exception.DDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.json.JSONObject;

public class UserTable {

  private static final Logger LOGGER = Logger.getLogger(UserTable.class.getName());

  private long userId;
  private String firebaseUserId;
  private String username;
  private boolean isUserPassphrase;
  private Boolean isConfigured;

  public UserTable() {}

  public UserTable(ResultSet rs) throws DDException {

    System.out.println(rs);
    try {
      while (rs.next()) {
        this.firebaseUserId = rs.getString("firebase_user_id");
        this.username = rs.getString("username");
        this.isUserPassphrase = rs.getBoolean("is_user_passphrase");
        this.userId = rs.getLong("user_id");
      }
    } catch (SQLException e) {
      throw new DDException("Error initialzing usertable", e);
    }
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long user_id) {
    this.userId = user_id;
  }

  public String getFirebaseUserId() {
    return firebaseUserId;
  }

  public UserTable setFirebaseUserId(String firebase_user_id) {
    this.firebaseUserId = firebase_user_id;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public UserTable setUsername(String username) {
    this.username = username;
    return this;
  }

  public boolean isUserPassphrase() {
    return isUserPassphrase;
  }

  public UserTable setUserPassphrase(boolean isUserPassphrase) {
    this.isUserPassphrase = isUserPassphrase;
    return this;
  }

  public Boolean getIsConfigured() {
    return isConfigured;
  }

  public UserTable setIsConfigured(Boolean isConfigured) {
    this.isConfigured = isConfigured;
    return this;
  }

  public JSONObject toJson() {
    return new JSONObject()
        .put("username", username)
        .put("uid", firebaseUserId)
        .put("isUserPassphrase", isUserPassphrase)
        .put("isConfigured", isConfigured);
  }

  public static UserTable addUser(String username, String uiHash, String passphrase)
      throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Adding new user : " + username + " : " + uiHash);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "INSERT INTO users (username, firebase_user_id, passphrase) values (?, ?, ?);");
      stmt.setString(1, username);
      stmt.setString(2, uiHash);
      stmt.setString(2, passphrase);
      stmt.executeUpdate();

      return new UserTable().setUsername(username).setFirebaseUserId(uiHash);
    } catch (SQLException e) {
      LOGGER.severe(e.getLocalizedMessage());
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static UserTable updatePassPhrase(boolean passPhrase, String uidHash) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Updating passphrase for : " + uidHash);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "UPDATE users set is_user_passphrase = ? WHERE firebase_user_id = ? ;");
      stmt.setBoolean(1, passPhrase);
      stmt.setString(2, uidHash);
      ResultSet rs = stmt.executeQuery();
      return new UserTable(rs);
    } catch (SQLException e) {
      LOGGER.severe(e.getLocalizedMessage());
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static UserTable getUserDetails(String uid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Fetching userDetails for : " + uid);
      conn = DBUtils.getConnection();
      stmt = conn.prepareStatement("SELECT * from users WHERE firebase_user_id = ? ;");
      stmt.setString(1, uid);
      ResultSet rs = stmt.executeQuery();
      if (rs == null) return null;
      return new UserTable(rs);
    } catch (SQLException e) {
      LOGGER.severe(e.getLocalizedMessage());

      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static boolean isUserConfigured(String uid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Fetching userDetails for : " + uid);
      conn = DBUtils.getConnection();
      stmt = conn.prepareStatement("SELECT * from users WHERE firebase_user_id = ? ;");
      stmt.setString(1, uid);
      ResultSet rs = stmt.executeQuery();
      return new UserTable(rs).isConfigured;
    } catch (SQLException e) {
      LOGGER.severe(e.getLocalizedMessage());

      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static UserTable updateUserName(String username, String uid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Updating username for : " + uid);
      conn = DBUtils.getConnection();
      stmt = conn.prepareStatement("UPDATE users set username = ? WHERE firebase_user_id = ? ;");
      stmt.setString(1, username);
      stmt.setString(2, uid);
      ResultSet rs = stmt.executeQuery();
      return new UserTable(rs);
    } catch (SQLException e) {
      LOGGER.severe(e.getLocalizedMessage());

      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }
}
