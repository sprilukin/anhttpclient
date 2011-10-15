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

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

/**
 * Basic {@link ClientConnectionManagerFactory} implementation
 *
 * @author Sergey Prilukin
 */
public final class ClientConnectionManagerFactoryImpl implements ClientConnectionManagerFactory {

    /**
     * Name of the property in {@link org.apache.http.params.HttpParams}
     * Which will be used to determine whether thread-safe connection manager should be used
     */
    public static final String THREAD_SAFE_CONNECTION_MANAGER = "thread.safe.connection.manager";

    /**
     * {@inheritDoc}
     */
    public ClientConnectionManager newInstance(HttpParams params, SchemeRegistry schemeRegistry) {
        if (params != null) {
            boolean threadSafe = params.getBooleanParameter(THREAD_SAFE_CONNECTION_MANAGER, false);
            return threadSafe ? new ThreadSafeClientConnManager(schemeRegistry) : new SingleClientConnManager(schemeRegistry);
        }

        return new SingleClientConnManager(schemeRegistry);
    }
}
