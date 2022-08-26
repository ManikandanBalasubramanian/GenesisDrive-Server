package com.test.restapi.handler;

import com.test.db.table.DataTable;
import com.test.exception.DDException;
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
    POST_API.put("/api/data/addMeta", DataHandler::addMeta);
    GET_API.put("/api/data/listMeta", DataHandler::listMeta);
    GET_API.put("/api/data/searchMeta", DataHandler::searchMeta);
    DELETE_API.put("/api/data/deleteMeta", DataHandler::deleteMeta);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    ResponseHandler.sendSuccessResponse(GET_API.get(path).apply(request), 200, response);
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
      String fileHash = request.getParameter("fileHash");
      String fileName = request.getParameter("fileName");
      String uid = request.getParameter("uid");
      int fileType = Integer.parseInt(request.getParameter("fileType"));
      boolean isEncrypted = Boolean.parseBoolean(request.getParameter("isEncrypted"));
      String searchTags = request.getParameter("searchTags");
      String cid = request.getParameter("cid");
      DataTable data =
          DataTable.addData(fileHash, fileName, uid, fileType, isEncrypted, searchTags, cid);
      return ResponseHandler.getSuccessResponseJson(data.toJson());
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject listMeta(HttpServletRequest request) {
    try {
      JSONArray dataList = new JSONArray();
      String uid = request.getParameter("uid");
      List<DataTable> list = DataTable.getList(uid);

      for (DataTable dt : list) {
        dataList.put(dt.toJson());
      }
      return ResponseHandler.getSuccessResponseJson(dataList);
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject deleteMeta(HttpServletRequest request) {
    try {
      String uid = request.getParameter("uid");
      String cid = request.getParameter("cid");
      int i = DataTable.deleteData(uid, cid);
      return ResponseHandler.getSuccessResponseJson("Deleted " + i + " data");
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject searchMeta(HttpServletRequest request) {
    try {
      JSONArray dataList = new JSONArray();
      String uid = request.getParameter("uid");
      String searchTerm = request.getParameter("search");
      List<DataTable> list = DataTable.searchData(uid, searchTerm);

      for (DataTable dt : list) {
        dataList.put(dt.toJson());
      }
      return ResponseHandler.getSuccessResponseJson(dataList);
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }
}
