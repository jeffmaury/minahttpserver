/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeffmaury.tools.mina.httpserver.Constants;
import com.jeffmaury.tools.mina.httpserver.FileHandler;

/**
 * A default empty processor used for methods that does not need to process a body (GET,...)
 * 
 * @author Jeff MAURY
 */
public class BaseProcessor implements Processor {
  
  private static final Logger logger = LoggerFactory.getLogger(BaseProcessor.class);
  
  /*
   * Flag that tell that the session should be close after processing is fully performed
   */
  private boolean mustCloseSession = false;

  /**
   * Name of the session attribute used to a flag telling is the response (headers) has already
   * been sent
   */
  public static final String RESPONSE_FLAG_ATTRIBUTE_NAME = BaseProcessor.class.getName() + ".responseFlag"; //$NON-NLS-1$

  protected HttpResponse getResponse(HttpRequest request) {
    return new DefaultHttpResponse(request.getProtocolVersion(), HttpStatus.SUCCESS_OK, Constants.NO_CONTENT_HEADERS);
  }
  
  public void processRequest(IoSession session, HttpRequest request)
      throws IOException {
    String connection = request.getHeader(Constants.CONNECTION_HEADER_NAME);
    /*
     * according to the HTTP specs, for HTTP1.1 connection is kept by default unless connection header is close
     *                              for HTTP1.0 connection is released by default unless connection header is keep-alive
     */
    mustCloseSession = (HttpVersion.HTTP_1_1.equals(request.getProtocolVersion()) && Constants.CONNECTION_HEADER_CLOSE.equals(connection)) ||
                       (HttpVersion.HTTP_1_0.equals(request.getProtocolVersion()) && !Constants.CONNECTION_HEADER_KEEP.equals(Constants.CONNECTION_HEADER_KEEP));
    HttpResponse response = getResponse(request);
    if (response != null) {
      writeResponse(session, response);
    }
  }

  public void processBodyMessage(IoSession session, IoBuffer message)
      throws IOException {
  }

  public void finish(IoSession session) throws IOException {
    session.removeAttribute(RESPONSE_FLAG_ATTRIBUTE_NAME);
    if (mustCloseSession) {
      logger.info("Closing session {} after request processing", session);
      session.close(false);
    }
  }

  /**
   * Write an HTTP response to the client if not already done.
   * 
   * @param session the MINA session
   * @param response the HTTP response
   * @throws IOException if error occurs
   */
  public void writeResponse(IoSession session, HttpResponse response) throws IOException {
    if (!session.containsAttribute(BaseProcessor.RESPONSE_FLAG_ATTRIBUTE_NAME)) {
      session.setAttribute(BaseProcessor.RESPONSE_FLAG_ATTRIBUTE_NAME);
      session.write(response);
    }
  }
}