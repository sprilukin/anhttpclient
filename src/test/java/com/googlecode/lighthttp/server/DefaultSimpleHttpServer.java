/*
 * Copyright (c) 2011 Sergey Prilukin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.googlecode.lighthttp.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper over Sun Java 6 HTTP Server
 * which is suitable for integration tests
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public final class DefaultSimpleHttpServer implements SimpleHttpServer {

    public static final String SERVER_HEADER_NAME = "Server";
    public static final String SERVER_NAME = "SimpleHttpServer";
    public static final String SERVER_VERSION = "0.1";
    public static final String FULL_SERVER_NAME = SERVER_NAME + "/" + SERVER_VERSION;
    public static final int DEFAULT_PORT = 8000;
    public static final String DEFAULT_HOST = "localhost";

    private HttpServer httpServer;
    private int port = DEFAULT_PORT;
    private Map<String, HttpHandler> handlers = new HashMap<String, HttpHandler>();
    private Map<String, String> defaultHeaders = new HashMap<String, String>();
    private final Object handlersMonitor = new Object();

    private HttpHandler defaultHandler = new HttpHandler() {

        private void logRequest(HttpExchange httpExchange) {
            //request URI
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST URI: " + httpExchange.getRequestURI().toString());

            //request headers
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST HEADERS: ");
            for (Map.Entry<String, List<String>> entry : httpExchange.getRequestHeaders().entrySet()) {
                for (String value: entry.getValue()) {
                    System.out.println(String.format("==== [%s: %s]", entry.getKey(), value));
                }
            }

            //request method
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST METHOD: " + httpExchange.getRequestMethod());

            //Writing request body
            //Skip because inputstream can be read here only once
            /*try {
                String request = new String(IOUtils.toByteArray(httpExchange.getRequestBody()));
                if (!"".equals(request.trim())) {
                    System.out.println("=== SIMPLE-HTTP-SERVER REQUEST BODY: \r\n" + request);
                } else {
                    System.out.println("=== SIMPLE-HTTP-SERVER REQUEST BODY IS EMPTY");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            logRequest(httpExchange);

            String path = httpExchange.getRequestURI().getPath();
            synchronized (handlersMonitor) {
                if (handlers.containsKey(path)) {
                    handlers.get(path).handle(httpExchange);
                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    httpExchange.close();
                }
            }
        }
    };

    public DefaultSimpleHttpServer() {
        defaultHeaders.put(SERVER_HEADER_NAME, FULL_SERVER_NAME);
    }

    private void createHttpServer() {
        if (httpServer == null) {
            synchronized (this) {
                if (httpServer == null) {
                    try {
                        httpServer = HttpServer.create();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    boolean bound = false;

                    while (!bound) {
                        try {
                            httpServer.bind(new InetSocketAddress(DEFAULT_HOST, port), 0);
                            bound = true;
                            System.out.println(String.format("=== SIMPLE-HTTP-SERVER ACCEPT CONNECTIONS ON PORT: %s", port));
                        } catch (BindException e) {
                            port++;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public String getBaseUrl() {
        return (new StringBuilder()).append("http://").append(DEFAULT_HOST).append(":").append(port).toString();
    }

    public void start() {
        createHttpServer();
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public SimpleHttpServer setPort(int port) {
        this.port = port;
        return this;
    }

    public SimpleHttpServer addHandler(String path, SimpleHttpHandler httpHandler) {
        createHttpServer();

        if (httpHandler != null) {
            for (Map.Entry<String, String> entry: defaultHeaders.entrySet()) {
                if (httpHandler.getResponseHeader(entry.getKey()) == null) {
                    httpHandler.setResponseHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        synchronized (handlersMonitor) {
            handlers.put(path, httpHandler);
        }

        httpServer.createContext(path, defaultHandler);
        return this;
    }

    public SimpleHttpServer setDefaultResponseHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders.putAll(defaultHeaders);
        return this;
    }

    public SimpleHttpServer addResponseHeader(String name, String value) {
        this.defaultHeaders.put(name, value);
        return this;
    }
}
