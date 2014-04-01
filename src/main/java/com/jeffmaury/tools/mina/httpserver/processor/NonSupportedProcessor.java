/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;

import static com.jeffmaury.tools.mina.httpserver.Constants.NO_CONTENT_HEADERS;
/**
 * Processor used for non supported HTTP methods.
 * 
 * @author Jeff MAURY
 *
 */
public class NonSupportedProcessor extends BaseProcessor {

  /**
   * {@inheritedDoc}
   */
  @Override
  protected HttpResponse getResponse(HttpRequest request) {
    return new DefaultHttpResponse(request.getProtocolVersion(), HttpStatus.CLIENT_ERROR_METHOD_NOT_ALLOWED, NO_CONTENT_HEADERS);
  }

}
