/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver;

import java.io.File;
import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpEndOfContent;
import org.apache.mina.http.api.HttpMethod;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeffmaury.tools.mina.httpserver.processor.BaseProcessor;
import com.jeffmaury.tools.mina.httpserver.processor.DeleteProcessor;
import com.jeffmaury.tools.mina.httpserver.processor.GetProcessor;
import com.jeffmaury.tools.mina.httpserver.processor.NonSupportedProcessor;
import com.jeffmaury.tools.mina.httpserver.processor.Processor;
import com.jeffmaury.tools.mina.httpserver.processor.PutProcessor;

/**
 * @author Jeff MAURY
 *
 */
public class FileHandler implements IoHandler {

  private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);
  
  private HttpServerConfiguration configuration;

  /**
   * Name of the session attribute used to store the processor
   */
  private static final String PROCESSOR_ATTRIBUTE_NAME = BaseProcessor.class.getName() + ".processor"; //$NON-NLS-1$

  /**
   * A processor for unsupported methods
   */
  protected static final Processor UNSUPPORTED_PROCESSOR = new NonSupportedProcessor();
  
  /**
   * @param configuration
   */
  public FileHandler(HttpServerConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * {@inheritedDoc}
   */
  public void exceptionCaught(IoSession session, Throwable t) throws Exception {
    logger.error("", t); //$NON-NLS-1$
    session.close(false);
  }

  /**
   * {@inheritedDoc}
   */
  public void messageReceived(IoSession session, Object message) throws Exception {
    logger.info("Message {} received on session {}", message.getClass(), session);
    if (message instanceof HttpRequest) {
      HttpRequest request = (HttpRequest) message;
      final File file = mapFile(request);
      Processor processor = UNSUPPORTED_PROCESSOR;
      /*
       * check if the method is supported and it the resource is valid. Send a response in the
       * negative case.
       */
        switch (request.getMethod()) {
          case GET:
            processor = new GetProcessor(file); 
            break;
          case DELETE:
            /*
             * processor is customized in order to return the proper HTTP status code according to the
             * HTTP spec
             */
            processor = new DeleteProcessor(file);
            break;
          case PUT:
            /*
             * processor is customized in order to return the proper HTTP status code according to the
             * HTTP spec
             */
            processor = new PutProcessor(file);
            break;
        }
      session.setAttribute(PROCESSOR_ATTRIBUTE_NAME, processor);
      processor.processRequest(session, request);
    } else if (message instanceof HttpEndOfContent) {
      ((Processor)session.getAttribute(PROCESSOR_ATTRIBUTE_NAME)).finish(session);
      session.removeAttribute(PROCESSOR_ATTRIBUTE_NAME);
    } else {
      ((Processor)session.getAttribute(PROCESSOR_ATTRIBUTE_NAME)).processBodyMessage(session, (IoBuffer) message);
    }
  }

  /**
   * A very minimal URL to file mapper. Should be a separate component for handling more complex
   * configurations (aka Apache httpd.conf).
   * 
   * @param request the HTTP request
   * @return the file handle
   */
  private File mapFile(HttpRequest request) {
    return new File(configuration.getBaseDir() + request.getRequestPath());
  }

  /**
   * {@inheritedDoc}
   */
  public void messageSent(IoSession session, Object message) throws Exception {
  }

  /**
   * {@inheritedDoc}
   */
  public void sessionClosed(IoSession session) throws Exception {
    logger.info("Session {} closed", session);
  }

  /**
   * {@inheritedDoc}
   */
  public void sessionCreated(IoSession session) throws Exception {
    logger.info("Session {} created", session);

  }

  /**
   * {@inheritedDoc}
   */
  public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    logger.info("Session {} idle status={}", session, status);
    session.close(false);
  }

  /**
   * {@inheritedDoc}
   */
  public void sessionOpened(IoSession session) throws Exception {
    logger.info("Session {} opened", session);
  }
}
