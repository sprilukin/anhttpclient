package com.googlecode.lighthttp.impl;

import com.googlecode.lighthttp.Cookie;
import com.googlecode.lighthttp.WebBrowser;
import com.googlecode.lighthttp.WebRequest;
import com.googlecode.lighthttp.WebResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Thread safe implementation of {@link com.googlecode.lighthttp.WebBrowser}
 * Use it if you do not want to rely on httpclient's thread safe client connection manager
 * (sometimes it won't works correctly)
 *
 * @author Sergey Pilukin
 * @since 11.05.2008 1:13:46
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
    
    public WebResponse getResponse(String url) throws IOException {
        return webBrowser.get().getResponse(url);
    }

    public WebResponse getResponse(String url, String expectedResponseCharset) throws IOException {
        return webBrowser.get().getResponse(url, expectedResponseCharset);
    }

    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        return webBrowser.get().getResponse(webRequest);
    }

    public WebResponse getResponse(WebRequest webRequest, String expectedResponseCharset) throws IOException {
        return webBrowser.get().getResponse(webRequest, expectedResponseCharset);
    }

    public Map<String, String> getHeaders() {
        return webBrowser.get().getHeaders();
    }

    public String getHeader(String headerName) {
        return webBrowser.get().getHeader(headerName);
    }

    public WebBrowser addHeaders(Map<String, String> headers) {
        webBrowser.get().addHeaders(headers);
        return this;
    }

    public WebBrowser addHeader(String name, String value) {
        webBrowser.get().addHeader(name, value);
        return this;
    }

    public WebBrowser setDefaultHeaders(Map defaultHeaders) {
        webBrowser.get().setDefaultHeaders(defaultHeaders);
        return this;
    }

    public Integer getRetryCount() {
        return webBrowser.get().getRetryCount();
    }

    public WebBrowser setRetryCount(Integer retryCount) {
        webBrowser.get().setRetryCount(retryCount);
        return this;
    }

    public Integer getSocketTimeout() {
        return webBrowser.get().getSocketTimeout();
    }

    public WebBrowser setSocketTimeout(Integer socketTimeout) {
        webBrowser.get().setSocketTimeout(socketTimeout);
        return this;
    }

    public Integer getConnectionTimeout() {
        return webBrowser.get().getConnectionTimeout();
    }

    public WebBrowser setConnectionTimeout(Integer connectionTimeout) {
        webBrowser.get().setConnectionTimeout(connectionTimeout);
        return this;
    }

    public List<Cookie> getCookies() {
        return webBrowser.get().getCookies();
    }

    public WebBrowser addCookie(Cookie cookie) {
        webBrowser.get().addCookie(cookie);
        return this;
    }

    public WebBrowser addCookies(List<Cookie> cookies) {
        webBrowser.get().addCookies(cookies);
        return this;
    }

    public WebBrowser clearAllCookies() {
        webBrowser.get().clearAllCookies();
        return this;
    }

    public WebBrowser setProxy(String url, int port) {
        webBrowser.get().setProxy(url, port);
        return this;
    }

    public WebBrowser clearProxy() {
        webBrowser.get().clearProxy();
        return this;
    }

    public WebBrowser abort() {
        webBrowser.get().abort();
        return this;
    }
}
