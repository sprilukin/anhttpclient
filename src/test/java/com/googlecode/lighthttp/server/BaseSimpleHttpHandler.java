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
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of {@link com.sun.net.httpserver.HttpHandler}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public abstract class BaseSimpleHttpHandler implements SimpleHttpHandler {
    private Map<String, String> headers = new HashMap<String, String>();

    public void setResponseHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            this.headers.putAll(headers);
        }
    }

    public String getResponseHeader(String name) {
        return headers.get(name);
    }

    public void setResponseHeader(String name, String value) {
        headers.put(name, value);
    }

    protected abstract byte[] getResponse(HttpExcahngeFacade httpExcahngeFacade);

    protected int getResponseCode(HttpExcahngeFacade httpExcahngeFacade) {
        return HttpURLConnection.HTTP_OK;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        HttpExcahngeFacade httpExcahngeFacade = new HttpExcahngeFacade(httpExchange);

        //Add headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry: headers.entrySet()) {
                httpExchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
            }
        }

        byte[] response = getResponse(httpExcahngeFacade);
        httpExchange.sendResponseHeaders(getResponseCode(httpExcahngeFacade), response != null ? response.length : 0);

        if (response != null && response.length > 0) {
            httpExchange.getResponseBody().write(response);
        }

        httpExchange.close();
    }
}
