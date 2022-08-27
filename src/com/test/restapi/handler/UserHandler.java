package com.test.restapi.handler;

import com.test.db.table.UserTable;
import com.test.exception.DDException;
import com.test.restapi.response.ResponseHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class UserHandler extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(UserHandler.class.getName());
  private static final long serialVersionUID = 1L;
  private static final Map<String, Function<HttpServletRequest, JSONObject>> GET_API =
      new HashMap<>();

  private static final Map<String, Function<HttpServletRequest, JSONObject>> POST_API =
      new HashMap<>();

  static {
    POST_API.put("/api/user/addUser", UserHandler::addUser);
    POST_API.put("/api/user/updatePassphrase", UserHandler::updatePassPhrase);
    POST_API.put("/api/user/updateUsername", UserHandler::updateUserName);
    GET_API.put("/api/user/getUserDetails", UserHandler::getUserDetails);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    LOGGER.info(path);
    ResponseHandler.sendSuccessResponse(GET_API.get(path).apply(request), 200, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    LOGGER.info(path);
    ResponseHandler.sendSuccessResponse(POST_API.get(path).apply(request), 200, response);
  }

  private static JSONObject addUser(HttpServletRequest request) {
    try {
      String userName = request.getParameter("username");
      String uid = request.getParameter("uid");

      return UserTable.addUser(userName, uid).toJson();
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject updateUserName(HttpServletRequest request) {
    try {
      String userName = request.getParameter("username");
      String uid = request.getParameter("uid");

      return UserTable.updateUserName(userName, uid).toJson();
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject updatePassPhrase(HttpServletRequest request) {
    try {
      boolean passphrase = Boolean.parseBoolean(request.getParameter("passphrase"));
      String uid = request.getParameter("uid");

      return UserTable.updatePassPhrase(passphrase, uid).toJson();
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }

  private static JSONObject getUserDetails(HttpServletRequest request) {
    try {
      String uid = request.getParameter("uid");
      return UserTable.getUserDetails(uid).toJson();
    } catch (DDException e) {
      return ResponseHandler.getErrorResponseJson(e.getMessage());
    }
  }
}
