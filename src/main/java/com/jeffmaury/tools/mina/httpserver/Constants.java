/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver;

import java.util.HashMap;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

/**
 * @author Jeff MAURY
 *
 */
public final class Constants {

  /**
   * Default headers used (minimal)
   */
  public static final Map<String, String> NO_CONTENT_HEADERS = new HashMap<String, String>();

  /**
   * Connection HTTP header
   */
  public static final String CONNECTION_HEADER_NAME = "connection"; //$NON-NLS-1$
  /**
   * Connection HTTP header close value
   */
  public static final String CONNECTION_HEADER_CLOSE = "close"; //$NON-NLS-1$
  /**
   * Connection HTTP header keep value
   */
  public static final String CONNECTION_HEADER_KEEP = "keep-alive"; //$NON-NLS-1$
  /**
   * Content-Type HTTP header
   */
  public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type"; //$NON-NLS-1$
  /*
   * Content-Length HTTP header
   */
  public static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length"; //$NON-NLS-1$
  /**
   * Encoding used for String
   */
  public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$

  /**
   * Static initialization
   */
  static {
    Constants.NO_CONTENT_HEADERS.put(CONTENT_LENGTH_HEADER_NAME, "0"); //$NON-NLS-1$
  }

  /**
   * Mime type registry used to generate content type for files
   */
  @SuppressWarnings("restriction")
  public static final FileTypeMap FILE_TYPE_MAP = new MimetypesFileTypeMap();
}
