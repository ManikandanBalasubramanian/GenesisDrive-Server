package com.test.restapi.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseHandler {

  public static final Logger LOGGER = Logger.getLogger(ResponseHandler.class.getName());

  public static final String STATUS = "status"; // No I18N
  public static final String MESSAGE = "message"; // No I18N
  public static final String ERROR = "error"; // No I18N

  public static final String SUCCESS = "success"; // No I18N
  public static final String FAILED = "failed"; // No I18N

  public static JSONObject getSuccessResponseJson(String message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, SUCCESS);
    response.put(MESSAGE, message);
    return response;
  }

  public static JSONObject getSuccessResponseJson(JSONObject message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, SUCCESS);
    response.put(MESSAGE, message);
    return response;
  }

  public static JSONObject getSuccessResponseJson(JSONArray message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, SUCCESS);
    response.put(MESSAGE, message);
    return response;
  }

  public static JSONObject getErrorResponseJson(String message) {
    JSONObject response = new JSONObject();
    response.put(STATUS, FAILED);
    response.put(ERROR, message);
    return response;
  }

  public static void sendSuccessResponse(
      JSONObject json, int statusCode, HttpServletResponse httpServletResponse) {
    httpServletResponse.setStatus(statusCode);
    writeResponse(json, httpServletResponse);
  }

  public static void sendErrorResponse(
      String message, int statusCode, HttpServletResponse httpServletResponse) {
    JSONObject response = new JSONObject();
    response.put(STATUS, FAILED);
    response.put(ERROR, message);
    httpServletResponse.setStatus(statusCode);
    writeResponse(response, httpServletResponse);
  }

  private static void writeResponse(JSONObject message, HttpServletResponse servletResponse) {
    try {
      servletResponse.setContentType("application/json");
      servletResponse.setCharacterEncoding("UTF-8");
      message.write(servletResponse.getWriter());
    } catch (JSONException | IOException e) {
      LOGGER.log(Level.SEVERE, "Unable write response for request", e);
    }
  }

  public static void sendFileResponse(String filePath, HttpServletResponse response)
      throws IOException {
    final File fileObj = new File(filePath);
    byte[] bytes = new byte[1024];
    response.setContentType("application/octet-stream"); // No I18N
    response.setStatus(200);

    try (FileInputStream inStream = new FileInputStream(fileObj);
        ServletOutputStream outStream = response.getOutputStream(); ) {
      int inbyte = 0;
      while ((inbyte = inStream.read(bytes)) != -1) {
        outStream.write(bytes, 0, inbyte);
      }
      outStream.flush();
    }
  }
}
