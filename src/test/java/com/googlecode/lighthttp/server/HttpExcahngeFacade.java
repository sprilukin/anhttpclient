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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Wrapper over {@link HttpExchange} instance
 * to allow access only for it's request related methods
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public final class HttpExcahngeFacade {
    private HttpExchange httpExchange;

    public HttpExcahngeFacade(HttpExchange httpExcahnge) {
        this.httpExchange = httpExcahnge;
    }

    public Object getAttribute(String key) {
        return httpExchange.getAttribute(key);
    }

    public Headers getRequestHeaders() {
        return httpExchange.getRequestHeaders();
    }

    public String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    public byte[] getRequestBody() throws IOException {
        return IOUtils.toByteArray(httpExchange.getRequestBody());
    }

    public URI getRequestURI() {
        return httpExchange.getRequestURI();
    }
}
