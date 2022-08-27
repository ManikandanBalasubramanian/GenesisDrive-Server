package com.test.restapi.handler;

import com.test.db.table.NFTTable;
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

public class NFTHandler extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(NFTHandler.class.getName());
  private static final long serialVersionUID = 1L;

  private static final Map<String, Function<HttpServletRequest, JSONObject>> GET_API =
      new HashMap<>();

  private static final Map<String, Function<HttpServletRequest, JSONObject>> POST_API =
      new HashMap<>();

  private static final Map<String, Function<HttpServletRequest, JSONObject>> DELETE_API =
      new HashMap<>();

  static {
    POST_API.put("/api/data/addMeta", NFTHandler::addMeta);
    GET_API.put("/api/data/listMeta", NFTHandler::listMeta);
    GET_API.put("/api/data/searchMeta", NFTHandler::searchMeta);
    DELETE_API.put("/api/data/deleteMeta", NFTHandler::deleteMeta);
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
      String uid = request.getParameter("uid");
      String cid = request.getParameter("cid");
      long dataId = request.getParameter("dataId");
      NFTTable data = NFTTable.addData(dataId, uid, cid);
      return ResponseHandler.getSuccessResponseJson(data.toJson());
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject listMeta(HttpServletRequest request) {
    try {
      JSONArray dataList = new JSONArray();
      String uid = request.getParameter("uid");
      List<NFTTable> list = NFTTable.getList(uid);

      for (NFTTable dt : list) {
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
      int i = NFTTable.deleteData(uid, cid);
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
      List<NFTTable> list = NFTTable.searchData(uid, searchTerm);

      for (NFTTable dt : list) {
        dataList.put(dt.toJson());
      }
      return ResponseHandler.getSuccessResponseJson(dataList);
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }
}
