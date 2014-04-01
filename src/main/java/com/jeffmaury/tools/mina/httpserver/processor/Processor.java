/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.HttpRequest;

/**
 * Method specific processor interface
 * 
 * @author Jeff MAURY
 */
public interface Processor {
  /**
   * Process the first phase of the HTTP request.
   * 
   * @param session the Mina session
   * @param request the HTTP request
   * @throws IOException if error occurs
   */
  public void processRequest(IoSession session, HttpRequest request) throws IOException;
  
  /**
   * Process part of the sent body by the requester.
   * 
   * @param session the Mina session
   * @param message the body part
   * @throws IOException if error occurs
   */
  public void processBodyMessage(IoSession session, IoBuffer message) throws IOException;
  
  /**
   * Perform final actions and cleanup when full body has been received.
   * 
   * @param session the Mina session
   * @throws IOException if error occurs
   */
  public void finish(IoSession session) throws IOException;
}