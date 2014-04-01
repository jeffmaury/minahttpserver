/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import java.io.File;

import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;

import static com.jeffmaury.tools.mina.httpserver.Constants.NO_CONTENT_HEADERS;

/**
 * DELETE method processor.
 * 
 * @author Jeff MAURY
 *
 */
public class DeleteProcessor extends BaseProcessor {
  /**
   * 
   */
  private final File file;

  /**
   * @param file
   */
  public DeleteProcessor(File file) {
    this.file = file;
  }

  @Override
  protected HttpResponse getResponse(HttpRequest request) {
    HttpStatus status = file.exists()?file.delete()?HttpStatus.SUCCESS_OK:HttpStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR:HttpStatus.CLIENT_ERROR_NOT_FOUND;
    return new DefaultHttpResponse(request.getProtocolVersion(), status, NO_CONTENT_HEADERS);
  }
}