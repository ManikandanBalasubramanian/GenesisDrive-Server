package com.test.restapi.handler;

import com.test.restapi.response.ResponseHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorResponseHandler extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = Logger.getLogger(ErrorResponseHandler.class.getName());

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
    if (throwable != null) {
      LOGGER.logp(
          Level.SEVERE,
          throwable.getStackTrace()[0].getClassName(),
          throwable.getStackTrace()[0].getMethodName(),
          throwable.getMessage(),
          throwable);
    }

    Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
    LOGGER.info("Handling Error : " + statusCode);
    handleGenericException(resp, statusCode);
  }

  public void handleGenericException(HttpServletResponse response, int statusCode) {

    switch (statusCode) {
      case 404:
        ResponseHandler.sendErrorResponse("URL Not Found", statusCode, response);
        break;
      case 408:
        ResponseHandler.sendErrorResponse("Request timeout", statusCode, response);
        break;
      case 414:
        ResponseHandler.sendErrorResponse("URI too large", statusCode, response);
        break;
      case 413:
        ResponseHandler.sendErrorResponse("Payload too large", statusCode, response);
        break;
      case 505:
        ResponseHandler.sendErrorResponse("HTTP Version Not Supported", statusCode, response);
        break;
      default:
        ResponseHandler.sendErrorResponse("Internal Server Error", statusCode, response);
        break;
    }
  }
}
