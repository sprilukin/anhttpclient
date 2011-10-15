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

package anhttp.server;

import sun.net.www.protocol.http.HttpURLConnection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter which implements most methods of {@link SimpleHttpHandler}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public abstract class SimpleHttpHandlerAdapter implements SimpleHttpHandler {
    private Map<String, String> headers = new HashMap<String, String>();

    public void setResponseHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            this.headers.putAll(headers);
        }
    }

    public Map<String, String> getResponseHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public void setResponseHeader(String name, String value) {
        headers.put(name, value);
    }

    public int getResponseCode(HttpRequestContext httpRequestContext) {
        return HttpURLConnection.HTTP_OK;
    }
}
