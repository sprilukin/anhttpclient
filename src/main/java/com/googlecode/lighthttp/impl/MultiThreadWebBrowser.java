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

package com.googlecode.lighthttp.impl;

import com.googlecode.lighthttp.Cookie;
import com.googlecode.lighthttp.WebBrowser;
import com.googlecode.lighthttp.WebRequest;
import com.googlecode.lighthttp.WebResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Thread safe implementation of {@link com.googlecode.lighthttp.WebBrowser}
 * Difference from ThreadLocalWebBrowser is that this implementation share cookies and other params
 *
 * @author Sergey Pilukin
 * @version $Id$
 */
public final class MultiThreadWebBrowser implements WebBrowser {
    private Map<String, WebBrowser> webBrowsersList = new HashMap<String, WebBrowser>(); 

    protected final Map<String, String> defaultHeaders = new HashMap<String, String>();
    protected int retryCount = WebBrowserConstants.DEFAULT_RETRY_COUNT;
    protected int socketTimeout = WebBrowserConstants.DEFAULT_SOCKET_TIMEOUT;
    protected int connectionTimeout = WebBrowserConstants.DEFAULT_CONNECTION_TIMEOUT;

    private final Object setRetryCountMonitor = new Object();
    private final Object setSocketTimeoutMonitor = new Object();
    private final Object setConnectionTimeoutMonitor = new Object();

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    private void initNewBrowser(WebBrowser webBrowser) {
        synchronized (defaultHeaders) {
                Map<String, String> headersProperties = new HashMap<String, String>(defaultHeaders);
                webBrowser.setDefaultHeaders(headersProperties);
        }
        synchronized (setRetryCountMonitor) {
            webBrowser.setRetryCount(retryCount);
        }
        synchronized (setSocketTimeoutMonitor) {
            webBrowser.setSocketTimeout(socketTimeout);
        }
        synchronized (setConnectionTimeoutMonitor) {
            webBrowser.setConnectionTimeout(connectionTimeout);
        }
    }

    private final Object getBrowserForCurrentThreadMonitor = new Object();

    private WebBrowser getBrowserForCurrentThread() {
        synchronized (getBrowserForCurrentThreadMonitor) {
            String threadName = getThreadName();
            if (webBrowsersList.get(threadName) == null) {
                DefaultWebBrowser defaultWebBrowser = new DefaultWebBrowser(false);
                initNewBrowser(defaultWebBrowser);
                webBrowsersList.put(threadName, defaultWebBrowser);
            }

            return webBrowsersList.get(threadName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(String url) throws IOException {
        return getBrowserForCurrentThread().getResponse(url);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(String url, String expectedResponseCharset) throws IOException {
        return getBrowserForCurrentThread().getResponse(url, expectedResponseCharset);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        return getBrowserForCurrentThread().getResponse(webRequest);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest, String expectedResponseCharset) throws IOException {
        return getBrowserForCurrentThread().getResponse(webRequest, expectedResponseCharset);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeaders() {
        return getBrowserForCurrentThread().getHeaders();
    }

    /**
     * {@inheritDoc}
     */
    public String getHeader(String headerName) {
        return getBrowserForCurrentThread().getHeader(headerName);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeaders(Map<String, String> headers) {
        synchronized (defaultHeaders) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.addHeaders(headers);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addHeader(String name, String value) {
        synchronized (defaultHeaders) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.addHeader(name, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        synchronized (this.defaultHeaders) {
            this.defaultHeaders.clear();
            for (Map.Entry<String, String> entryObject : defaultHeaders.entrySet()) {
                String headerName = entryObject.getKey();
                String headerValue = entryObject.getValue();
                this.defaultHeaders.put(headerName, headerValue);
            }

            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setDefaultHeaders(defaultHeaders);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultHeaders(Properties defaultHeaders) {
        synchronized (this.defaultHeaders) {
            this.defaultHeaders.clear();
            for (Map.Entry<Object, Object> entryObject : defaultHeaders.entrySet()) {
                String headerName = String.valueOf(entryObject.getKey());
                String headerValue = String.valueOf(entryObject.getValue());
                this.defaultHeaders.put(headerName, headerValue);
            }

            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setDefaultHeaders(defaultHeaders);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer getRetryCount() {
        return getBrowserForCurrentThread().getRetryCount();
    }

    /**
     * {@inheritDoc}
     */
    public void setRetryCount(Integer retryCount) {
        synchronized (setRetryCountMonitor) {
            this.retryCount = retryCount;

            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setRetryCount(retryCount);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer getSocketTimeout() {
        return getBrowserForCurrentThread().getSocketTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setSocketTimeout(Integer socketTimeout) {
        synchronized (setSocketTimeoutMonitor) {
            this.socketTimeout = socketTimeout;

            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setSocketTimeout(socketTimeout);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer getConnectionTimeout() {
        return getBrowserForCurrentThread().getConnectionTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setConnectionTimeout(Integer connectionTimeout) {
        synchronized (setConnectionTimeoutMonitor) {
            this.connectionTimeout = connectionTimeout;

            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setConnectionTimeout(connectionTimeout);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Cookie> getCookies() {
        return getBrowserForCurrentThread().getCookies();
    }

    private final Object cookieMonitor = new Object();

    /**
     * {@inheritDoc}
     */
    public void addCookie(Cookie cookie) {
        synchronized (cookieMonitor) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.addCookie(cookie);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addCookies(List<Cookie> cookies) {
        synchronized (cookieMonitor) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.addCookies(cookies);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearAllCookies() {
        synchronized (cookieMonitor) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.clearAllCookies();
            }
        }
    }

    private final Object setProxyMonitor = new Object();

    /**
     * {@inheritDoc}
     */
    public void setProxy(String url, int port) {
        synchronized (setProxyMonitor) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.setProxy(url, port);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearProxy() {
        synchronized (setProxyMonitor) {
            for (WebBrowser webBrowser: webBrowsersList.values()) {
                webBrowser.clearProxy();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void abort() {
        throw new RuntimeException("Not implemented");
    }
}
