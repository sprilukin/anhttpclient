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

package anhttp.impl;

/**
 * Constants for usage in implementations of {@link anhttp.WebBrowser}
 *
 * @author Sergey Prilukin
 */
public final class WebBrowserConstants {

    /**
     * Default user-agent.
     * May be replaced with {@code User-Agent} request header
     */
    public static final String DEFAULT_USER_AGENT = "anhttp/0.2";

    /**
     * Default count if retrying unsuccessful HTTP request
     */
    public static final int DEFAULT_RETRY_COUNT = 3;

    /**
     * Default request socket timeout
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000;

    /**
     * Default connection timeout
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;

    /**
     * Default class name of implementation of
     * {@link org.apache.http.conn.ClientConnectionManagerFactory}
     */
    public static final String DEFAULT_CLIENT_CONNECTION_FACTORY_CLASS_NAME =
            "anhttp.impl.ClientConnectionManagerFactoryImpl";
}
