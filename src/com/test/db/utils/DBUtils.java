package com.test.db.utils;

import com.test.db.configurations.DBConfigurations;
import com.test.exception.DDException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBUtils {

  public static void main(String[] args) {
    System.out.println(new Timestamp(System.currentTimeMillis()));
  }

  public static Connection getConnection() throws DDException {
    try {
      Class.forName("org.postgresql.Driver");
      return DriverManager.getConnection(DBConfigurations.url, DBConfigurations.props);
    } catch (SQLException | ClassNotFoundException e) {
      throw new DDException(
          DDException.StatusCode.INTERNAL_SERVER_ERROR,
          "Internal Server Error",
          "Error getting DBConnection",
          e);
    }
  }

  public static void closeConnection(Connection conn) throws DDException {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new DDException(
          DDException.StatusCode.INTERNAL_SERVER_ERROR,
          "Internal Server Error",
          "Error closing DBConnection",
          e);
    }
  }

  public static void closeStatement(PreparedStatement stmt) throws DDException {
    try {
      stmt.close();
    } catch (SQLException e) {
      throw new DDException(
          DDException.StatusCode.INTERNAL_SERVER_ERROR,
          "Internal Server Error",
          "Error closing DBConnection",
          e);
    }
  }
}
