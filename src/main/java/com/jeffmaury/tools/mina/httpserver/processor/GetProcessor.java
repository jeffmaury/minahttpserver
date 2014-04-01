/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver.processor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;

import static com.jeffmaury.tools.mina.httpserver.Constants.CONTENT_LENGTH_HEADER_NAME;
import static com.jeffmaury.tools.mina.httpserver.Constants.CONTENT_TYPE_HEADER_NAME;
import static com.jeffmaury.tools.mina.httpserver.Constants.FILE_TYPE_MAP;
import static com.jeffmaury.tools.mina.httpserver.Constants.UTF_8;

/**
 * A processor that can read files or directories. Used for GET method.
 * 
 * @author Jeff MAURY
 */
public class GetProcessor extends BaseProcessor {
  private File file;

  public GetProcessor(File file) {
    this.file = file;
  }

  protected String getStringRepresentation(File directory) {
    return "<html><body><p>Directory " + directory.getAbsolutePath() + "</p></body></html>";
  }
  
  /**
   * {@inheritedDoc}
   */
  @Override
  protected HttpResponse getResponse(HttpRequest request) {
    /*
     * response is sent by our processRequest implementation so return null in order
     * to prevent base implementation not to send a default response
     */
    return null;
  }

  @Override
  public void processRequest(IoSession session, HttpRequest request)
      throws IOException {
    Object message = null;
    HttpStatus status = HttpStatus.SUCCESS_OK;
    super.processRequest(session, request);
    Map<String, String> headers = new HashMap<String, String>();
    if (file.exists()) {
      if (file.isFile()) {
        headers.put(CONTENT_LENGTH_HEADER_NAME, Long.toString(file.length()));
        headers.put(CONTENT_TYPE_HEADER_NAME,
            FILE_TYPE_MAP.getContentType(file) + ";charset="
                + Charset.defaultCharset().name());
        message = file;
      } else {
        String str = getStringRepresentation(file);
        IoBuffer buffer = IoBuffer.allocate(str.length());
        buffer.putString(str, Charset.forName(UTF_8).newEncoder());
        buffer.rewind();
        headers.put(CONTENT_LENGTH_HEADER_NAME, Integer.toString(str.length()));
        headers.put(CONTENT_TYPE_HEADER_NAME, "text/plain; charset=" + UTF_8);
        message = buffer;
      }
    } else {
      status = HttpStatus.CLIENT_ERROR_NOT_FOUND;
    }
    writeResponse(session, new DefaultHttpResponse(request.getProtocolVersion(), status, headers));
    if (message != null) {
      session.write(message);
    }
  }
}