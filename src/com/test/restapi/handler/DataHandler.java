package com.test.restapi.handler;

import com.google.firebase.auth.FirebaseAuthException;
import com.test.db.table.DataTable;
import com.test.exception.DDException;
import com.test.firebase.FirebaseHandler;
import com.test.restapi.response.ResponseHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataHandler extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(DataHandler.class.getName());
  private static final long serialVersionUID = 1L;

  private static final Map<String, Function<HttpServletRequest, JSONObject>> GET_API =
      new HashMap<>();

  private static final Map<String, Function<HttpServletRequest, JSONObject>> POST_API =
      new HashMap<>();

  private static final Map<String, Function<HttpServletRequest, JSONObject>> DELETE_API =
      new HashMap<>();

  static {
    POST_API.put("/api/data/add", DataHandler::addMeta);
    POST_API.put("/api/data/list", DataHandler::listMeta);
    POST_API.put("/api/data/search", DataHandler::searchMeta);
    DELETE_API.put("/api/data/delete", DataHandler::deleteMeta);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    ResponseHandler.sendSuccessResponse(POST_API.get(path).apply(request), 200, response);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    ResponseHandler.sendSuccessResponse(DELETE_API.get(path).apply(request), 200, response);
  }

  private static JSONObject addMeta(HttpServletRequest request) {
    try {
      LOGGER.info("Add Meta");
      JSONObject json = AuthenticationHandler.parseJson(request);
      String idToken = json.getString("idToken");

      String fileHash = json.getString("fileHash");
      String fileName = json.getString("fileName");
      String uidHash = FirebaseHandler.uidHash(FirebaseHandler.getUID(idToken));
      int fileType = Integer.parseInt(request.getParameter("fileType"));
      boolean isEncrypted = Boolean.parseBoolean(request.getParameter("isEncrypted"));
      String searchTags = request.getParameter("searchTags");
      String cid = request.getParameter("cid");
      DataTable data =
          DataTable.addData(fileHash, fileName, uidHash, fileType, isEncrypted, searchTags, cid);
      return ResponseHandler.getSuccessResponseJson(data.toJson());
    } catch (DDException | FirebaseAuthException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject listMeta(HttpServletRequest request) {
    try {
      JSONArray dataList = new JSONArray();
      String idToken = request.getParameter("idToken");
      String uidHash = FirebaseHandler.uidHash(FirebaseHandler.getUID(idToken));

      List<DataTable> list = DataTable.getList(uidHash);

      for (DataTable dt : list) {
        dataList.put(dt.toJson());
      }
      return ResponseHandler.getSuccessResponseJson(dataList);
    } catch (DDException | FirebaseAuthException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject deleteMeta(HttpServletRequest request) {
    try {
      String idToken = request.getParameter("idToken");
      String uidHash = FirebaseHandler.uidHash(FirebaseHandler.getUID(idToken));

      String cid = request.getParameter("cid");
      int i = DataTable.deleteData(uidHash, cid);
      return ResponseHandler.getSuccessResponseJson("Deleted " + i + " data");
    } catch (DDException | FirebaseAuthException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject searchMeta(HttpServletRequest request) {
    try {
      JSONArray dataList = new JSONArray();
      String uid = request.getParameter("uid");
      String uidHash = FirebaseHandler.uidHash(uid);

      String searchTerm = request.getParameter("search");
      List<DataTable> list = DataTable.searchData(uidHash, searchTerm);

      for (DataTable dt : list) {
        dataList.put(dt.toJson());
      }
      return ResponseHandler.getSuccessResponseJson(dataList);
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }
}
