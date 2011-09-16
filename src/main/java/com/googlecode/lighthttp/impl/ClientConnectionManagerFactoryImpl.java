package com.googlecode.lighthttp.impl;

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
 *         Date: 16.09.11 17:54
 */
public final class ClientConnectionManagerFactoryImpl implements ClientConnectionManagerFactory {
    public static final String THREAD_SAFE_CONNECTION_MANAGER = "thread.safe.connection.manager";

    public ClientConnectionManager newInstance(HttpParams params, SchemeRegistry schemeRegistry) {
        if (params != null) {
            boolean threadSafe = params.getBooleanParameter(THREAD_SAFE_CONNECTION_MANAGER, false);
            return threadSafe ? new ThreadSafeClientConnManager(schemeRegistry) : new SingleClientConnManager(schemeRegistry);
        }

        return new SingleClientConnManager(schemeRegistry);
    }
}
