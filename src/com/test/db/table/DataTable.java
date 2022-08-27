package com.test.db.table;

import com.test.db.utils.DBUtils;
import com.test.exception.DDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONObject;

public class DataTable {

  private static final Logger LOGGER = Logger.getLogger(DataTable.class.getName());

  private long dataId;
  private long userId;

  private String fileHash;
  private String fileName;

  private int fileType;
  private boolean isEncrypted;

  private String searchTags;
  private String cid;

  private Timestamp uploadedTime;
  private Timestamp lastAccessed;

  public static DataTable getDT(ResultSet rs) throws DDException {
    List<DataTable> list = getDTList(rs);
    if (list == null || list.size() == 0) return new DataTable();
    return list.get(0);
  }

  public static List<DataTable> getDTList(ResultSet rs) throws DDException {
    try {
      List<DataTable> list = new ArrayList<>();
      while (rs.next()) {
        DataTable dt = new DataTable();
        dt.dataId = rs.getLong("data_id");
        dt.userId = rs.getLong("user_id");
        dt.fileHash = rs.getString("file_hash");
        dt.fileName = rs.getString("file_name");
        dt.fileType = rs.getInt("file_type");
        dt.isEncrypted = rs.getBoolean("is_encrypted");
        dt.searchTags = rs.getString("search_tags");
        dt.cid = rs.getString("cid");
        dt.setLastAccessed(rs.getTimestamp("last_accessed_on"));
        dt.setUploadedTime(rs.getTimestamp("uploaded_on"));
        list.add(dt);
      }

      return list;
    } catch (SQLException e) {
      throw new DDException("Error initialzing datatable", e);
    }
  }

  public DataTable() {}

  public long getDataId() {
    return dataId;
  }

  public void setDataId(long dataId) {
    this.dataId = dataId;
  }

  public String getFileHash() {
    return fileHash;
  }

  public void setFileHash(String fileHash) {
    this.fileHash = fileHash;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public int getFileType() {
    return fileType;
  }

  public void setFileType(int fileType) {
    this.fileType = fileType;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public boolean getIsEncrypted() {
    return isEncrypted;
  }

  public void setIsEncrypted(boolean isEncrypted) {
    this.isEncrypted = isEncrypted;
  }

  public String getSearchTags() {
    return searchTags;
  }

  public void setSearchTags(String searchTags) {
    this.searchTags = searchTags;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public Timestamp getUploadedTime() {
    return uploadedTime;
  }

  public void setUploadedTime(Timestamp uploadedTime) {
    this.uploadedTime = uploadedTime;
  }

  public Timestamp getLastAccessed() {
    return lastAccessed;
  }

  public void setLastAccessed(Timestamp lastAccessed) {
    this.lastAccessed = lastAccessed;
  }

  public JSONObject toJson() {
    return new JSONObject()
        .put("fileName", fileName)
        .put("fileHash", fileHash)
        .put("isEncrypted", isEncrypted)
        .put("cid", cid)
        .put("uploadedOn", uploadedTime)
        .put("lastAccessed", lastAccessed);
  }

  public static DataTable addData(
      String fileHash,
      String fileName,
      String uid,
      int fileType,
      boolean isEncrypted,
      String searchTags,
      String cid)
      throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      Timestamp upload = new Timestamp(System.currentTimeMillis());
      LOGGER.info("Adding new data : " + uid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "INSERT INTO contents (file_hash, file_name, file_type, is_encrypted, search_tags, cid, uploaded_on, last_accessed_on, user_id) values (?, ?, ?, ?, ?, ? ,?, ?, (SELECT user_id from users WHERE firebase_user_id = ?));");
      stmt.setString(1, fileHash);
      stmt.setString(2, fileName);
      stmt.setInt(3, fileType);
      stmt.setBoolean(4, isEncrypted);
      stmt.setString(5, searchTags);
      stmt.setString(6, cid);
      stmt.setTimestamp(7, upload);
      stmt.setTimestamp(8, upload);

      stmt.setString(9, uid);
      ResultSet rs = stmt.executeQuery();
      return getDT(rs);
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static List<DataTable> getList(String uid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Listing Data : " + uid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "SELECT * from contents WHERE user_id = (SELECT user_id from users WHERE firebase_user_id = ?);");
      stmt.setString(1, uid);
      ResultSet rs = stmt.executeQuery();
      return getDTList(rs);
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static List<DataTable> searchData(String uid, String search) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Searching Data : " + uid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "SELECT * from contents WHERE search_tags LIKE %?% AND user_id = (SELECT user_id from users WHERE firebase_user_id = ?);");
      stmt.setString(1, search);
      stmt.setString(2, uid);
      ResultSet rs = stmt.executeQuery();
      return getDTList(rs);
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static int deleteData(String uid, String cid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Deleting Data : " + cid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "DELETE from contents WHERE cid = ? AND user_id = (SELECT user_id from users WHERE firebase_user_id = ?);");
      stmt.setString(1, cid);
      stmt.setString(2, uid);
      int deleted = stmt.executeUpdate();

      return deleted;
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }
}
