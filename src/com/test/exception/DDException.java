package com.test.exception;

public class DDException extends Exception {

  public static enum StatusCode {
    /** Successfully processed the request */
    OK(200),
    /** The data provided by the client is invalid or not acceptable */
    BAD_REQUEST(400),
    /** The user does not have the permission to access the resource */
    FORBIDDEN(403),
    /** Something unexpected happens in our side */
    INTERNAL_SERVER_ERROR(500);

    private StatusCode(int arg) {
      this.code = arg;
    }

    int code;

    public int getCode() {
      return code;
    }
  }

  private static final long serialVersionUID = 1L;

  private final StatusCode statusCode;

  private final String debugMessage;

  public DDException(StatusCode statusCode, String message, String debugMessage, Throwable cause) {
    super(message, cause);
    this.debugMessage = debugMessage;
    this.statusCode = statusCode;
  }

  public DDException(String debugMessage, Throwable cause) {
    super(StatusCode.INTERNAL_SERVER_ERROR.name(), cause);
    this.debugMessage = debugMessage;
    this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
  }

  public DDException(StatusCode statusCode, String message, Throwable cause) {
    super(message, cause);
    this.debugMessage = message;
    this.statusCode = statusCode;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  @Override
  public String getLocalizedMessage() {
    return this.debugMessage;
  }
}
