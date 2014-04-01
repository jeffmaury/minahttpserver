/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpStatus;

import static com.jeffmaury.tools.mina.httpserver.Constants.NO_CONTENT_HEADERS;

/**
 * PUT method processor.
 * 
 * @author Jeff MAURY
 */
public class PutProcessor extends BaseProcessor {
  /**
   * 
   */
  private final File file;
  private OutputStream os = null;
  private DefaultHttpResponse response;

  /**
   * @param file
   */
  public PutProcessor(File file) {
    this.file = file;
  }

  @Override
  public void processRequest(IoSession session, HttpRequest request)
      throws IOException {
    /*
     * create the directory hierarchy only if it does not exist. if the operation
     * fails, return an error status code and ignore the rest of the processing
     */
    boolean result = file.getParentFile().exists();
    result = result?result:file.getParentFile().mkdirs();
    if (result) {
      os = new FileOutputStream(file);
    }
    /*
     * delay sending the response to the end of the processing
     */
    response = new DefaultHttpResponse(request.getProtocolVersion(), result?HttpStatus.SUCCESS_CREATED:HttpStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR, NO_CONTENT_HEADERS);

  }

  /**
   * {@inheritedDoc}
   */
  @Override
  public void processBodyMessage(IoSession session, IoBuffer message)
      throws IOException {
    if (os != null) {
      os.write(message.array());
    }
  }

  /**
   * {@inheritedDoc}
   */
  @Override
  public void finish(IoSession session) throws IOException {
    writeResponse(session, response);
    super.finish(session);
    if (os != null) {
      try {
        os.close();
      } catch (IOException e) {
      }
    }
  }
}