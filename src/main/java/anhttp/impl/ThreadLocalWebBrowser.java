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

import anhttp.WebBrowser;
import anhttp.WebRequest;
import anhttp.WebResponse;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Thread safe implementation of {@link anhttp.WebBrowser}
 * Use it if you do not want to rely on httpclient's thread safe client connection manager
 * (sometimes it won't works correctly)
 *
 * @author Sergey Pilukin
 */
public final class ThreadLocalWebBrowser implements WebBrowser {

    private static WebBrowser instance = null;
    
    protected ThreadLocal<WebBrowser> webBrowser = new ThreadLocal<WebBrowser>() {
        @Override
        protected synchronized WebBrowser initialValue() {
            return new DefaultWebBrowser(false);
        }
    };

    private static final Object createInstanceMonitor = new Object();
    
    public static WebBrowser getInstance() {
        if (ThreadLocalWebBrowser.instance == null) {
            synchronized (createInstanceMonitor) {
                if (ThreadLocalWebBrowser.instance == null) {
                    ThreadLocalWebBrowser.instance = new ThreadLocalWebBrowser();
                }
            }
        }

        return ThreadLocalWebBrowser.instance;
    }
    
    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(String url) throws IOException {
        return webBrowser.get().getResponse(url);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(String url, String expectedResponseCharset) throws IOException {
        return webBrowser.get().getResponse(url, expectedResponseCharset);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        return webBrowser.get().getResponse(webRequest);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest, String expectedResponseCharset) throws IOException {
        return webBrowser.get().getResponse(webRequest, expectedResponseCharset);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeaders() {
        return webBrowser.get().getHeaders();
    }

    /**
     * {@inheritDoc}
     */
    public String getHeader(String headerName) {
        return webBrowser.get().getHeader(headerName);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeaders(Map<String, String> headers) {
        webBrowser.get().addHeaders(headers);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeader(String name, String value) {
        webBrowser.get().addHeader(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        webBrowser.get().setDefaultHeaders(defaultHeaders);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultHeaders(Properties defaultHeaders) {
        webBrowser.get().setDefaultHeaders(defaultHeaders);
    }

    /**
     * {@inheritDoc}
     */
    public Integer getRetryCount() {
        return webBrowser.get().getRetryCount();
    }

    /**
     * {@inheritDoc}
     */
    public void setRetryCount(Integer retryCount) {
        webBrowser.get().setRetryCount(retryCount);
    }

    /**
     * {@inheritDoc}
     */
    public Integer getSocketTimeout() {
        return webBrowser.get().getSocketTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setSocketTimeout(Integer socketTimeout) {
        webBrowser.get().setSocketTimeout(socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    public Integer getConnectionTimeout() {
        return webBrowser.get().getConnectionTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setConnectionTimeout(Integer connectionTimeout) {
        webBrowser.get().setConnectionTimeout(connectionTimeout);
    }

    /**
     * {@inheritDoc}
     */
    public List<Cookie> getCookies() {
        return webBrowser.get().getCookies();
    }

    /**
     * {@inheritDoc}
     */
    public Cookie getCookieByName(String name) {
        return webBrowser.get().getCookieByName(name);
    }

    /**
     * {@inheritDoc}
     */
    public void addCookie(Cookie cookie) {
        webBrowser.get().addCookie(cookie);
    }

    /**
     * {@inheritDoc}
     */
    public void addCookies(List<Cookie> cookies) {
        webBrowser.get().addCookies(cookies);
    }

    /**
     * {@inheritDoc}
     */
    public void clearAllCookies() {
        webBrowser.get().clearAllCookies();
    }

    /**
     * {@inheritDoc}
     */
    public void setProxy(String url, int port) {
        webBrowser.get().setProxy(url, port);
    }

    /**
     * {@inheritDoc}
     */
    public void clearProxy() {
        webBrowser.get().clearProxy();
    }

    /**
     * {@inheritDoc}
     */
    public void abort() {
        webBrowser.get().abort();
    }
}
