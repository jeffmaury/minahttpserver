/**
 * 
 */
package com.jeffmaury.tools.mina.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * A simple HTTP server based on Apache MINA.
 * 
 * Configuration is managed by a separated HttpServerConfiguration object which is
 * immutable.
 * 
 * @author Jeff MAURY
 *
 */
public class HttpServer {

  /**
   * The associated configuration object.
   */
  private HttpServerConfiguration configuration;
  
  /**
   * The MINA socket acceptor
   */
  private SocketAcceptor acceptor;

  /**
   * Build an HTTP server from the configuration.
   * 
   * @param configuration the configuration object to use
   */
  public HttpServer(HttpServerConfiguration configuration) {
    this.configuration = configuration;
  }
  
  /**
   * Start the HTTP server. Socket listened will be initiated here.
   * 
   * @throws IOException
   */
  public void start() throws IOException {
    acceptor = new NioSocketAcceptor(configuration.getIoThreadsNumber());

    /*
     * add a filter that will process the event in a thread different from the IO thread
     */
    acceptor.getFilterChain().addFirst("executor", new ExecutorFilter(configuration.getHandlerThreadsNumber())); //$NON-NLS-1$
    
    /*
     * Add the HTTP filter to allow HTTP requests decoding and HTTP responses
     * encoding (provided by MINA HTTP)
     */
    acceptor.getFilterChain().addLast("http", new HttpServerCodec()); //$NON-NLS-1$
    /**
     * Set our own handler that fill process the HTTP requests and all of the HTTP server
     * logic
     */
    acceptor.setHandler(new FileHandler(configuration));
    
    acceptor.getSessionConfig().setReaderIdleTime(configuration.getTimeout());
    /*
     * start the job
     */
    acceptor.bind(new InetSocketAddress(configuration.getPort()));
  }

  /**
   * Stop the HTTP server.
   */
  public void stop() {
    acceptor.dispose();
  }
}
