package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * The ResponseException for whenever there is a message on the Server and
 * we need to catch it and return it from the handlers.
 */
public class ResponseException extends Exception {
  static private int statusCode;

  /**
   * The constructor for the ResponseException given a specific statusCode
   * and the error message.
   *
   * @param statusCode the HTTP status code
   * @param message the message of the error
   */
  public ResponseException(int statusCode, String message) {
    super(message);
    ResponseException.statusCode = statusCode;
  }

  /**
   * Makes a ResponseException from a Json InputStream.
   *
   * @param stream the InputStream with the Json error response
   * @return a ResponseException with the statusCode and message from Json
   */
  public static ResponseException fromJson(InputStream stream) {
    var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
    String message = map.get("message").toString();
    return new ResponseException(statusCode, message);
  }
}