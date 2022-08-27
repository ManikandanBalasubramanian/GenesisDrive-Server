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

public class NFTTable {

  private static final Logger LOGGER = Logger.getLogger(NFTTable.class.getName());

  private long id;
  private long dataId;
  private Timestamp createdOn;
  private String cid;
  private DataTable dataTable;

  public static NFTTable getNFT(ResultSet rs) throws DDException {
    List<NFTTable> list = getNFTList(rs);
    if (list == null || list.size() == 0) return new NFTTable();
    return list.get(0);
  }

  public static List<NFTTable> getNFTList(ResultSet rs) throws DDException {
    try {
      List<NFTTable> list = new ArrayList<>();
      while (rs.next()) {
        NFTTable dt = new NFTTable();
        DataTable dat = new DataTable();

        dat.setDataId(rs.getLong("data_id"));
        dat.setUserId(rs.getLong("user_id"));
        dat.setFileHash(rs.getString("file_hash"));
        dat.setFileName(rs.getString("file_name"));
        dat.setFileType(rs.getInt("file_type"));
        dat.setIsEncrypted(rs.getBoolean("is_encrypted"));
        dat.setSearchTags(rs.getString("search_tags"));
        dat.setCid(rs.getString("cid"));

        dt.dataTable = dat;
        dt.id = rs.getLong("id");
        dt.dataId = rs.getLong("data_id");
        dt.cid = rs.getString("cid");
        dt.createdOn = rs.getTimestamp("created_on");

        list.add(dt);
      }

      return list;
    } catch (SQLException e) {
      throw new DDException("Error initialzing datatable", e);
    }
  }

  public NFTTable() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public long getDataId() {
    return dataId;
  }

  public void setDataId(long dataId) {
    this.dataId = dataId;
  }

  public Timestamp getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  public JSONObject toJson() {
    return dataTable.toJson().put("cid", cid).put("createdOn", createdOn);
  }

  public static NFTTable addData(long dataId, String uid, String cid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      Timestamp upload = new Timestamp(System.currentTimeMillis());
      LOGGER.info("Adding new NFT : " + cid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "INSERT INTO nft_contents (cid, created_on, data_id) values (?, ?, (SELECT data_id from contents WHERE uid = (SELECT user_id from users WHERE firebase_user_id = ?)));");
      stmt.setString(1, cid);
      stmt.setTimestamp(2, upload);
      stmt.setString(3, uid);

      ResultSet rs = stmt.executeQuery();
      return getNFT(rs);
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static List<NFTTable> getList(String uid) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Listing NFT : " + uid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "SELECT * from nft_contents JOIN contents USING (data_id) WHERE user_id = (SELECT user_id from users WHERE firebase_user_id = ?);");
      stmt.setString(1, uid);
      ResultSet rs = stmt.executeQuery();
      return getNFTList(rs);
    } catch (SQLException e) {
      throw new DDException("Error fetching userdetails!", e);
    } finally {
      DBUtils.closeConnection(conn);
      DBUtils.closeStatement(stmt);
    }
  }

  public static List<NFTTable> searchData(String uid, String search) throws DDException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      LOGGER.info("Searching NFT : " + uid);
      conn = DBUtils.getConnection();
      stmt =
          conn.prepareStatement(
              "SELECT * from nft_contents JOIN contents USING (data_id) WHERE search_tags LIKE ?  AND user_id = (SELECT user_id from users WHERE firebase_user_id = ?);");
      stmt.setString(1, "%" + search + "%");
      stmt.setString(2, uid);
      ResultSet rs = stmt.executeQuery();
      return getNFTList(rs);
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
              "DELETE from nft_contents WHERE data_id = (SELECT data_id from contents WHERE cid = ? AND user_id = (SELECT user_id from users WHERE firebase_user_id = ?));");
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
