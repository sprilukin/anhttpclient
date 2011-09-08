package com.googlecode.lighthttp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for creating and using easy http client
 * with minimum necessary methods
 *
 * @author Sergey Prilukin
 * @since 08.04.2008 17:20:21
 */
public interface WebBrowser {

    /**
     * Return {@link WebResponse} which is shell on http response for specified url.
     * ISO-8859-1 will be used as response content charset
     *
     * @param url desired url
     * @return {@link WebResponse} which wraps http response of request from this url
     * @throws IOException if transport or protocol exceptions occurs
     */
    public WebResponse getResponse(String url) throws IOException;

    /**
     * Return {@link WebResponse} which is shell on http response for specified url
     *
     * @param url desired url
     * @param expectedResponseCharset charset which is expected in text of response
     * ISO-8859-1 will be used as response content charset if it will be {@code null}
     * @return {@link WebResponse} which wraps http response of request from this url
     * @throws IOException if transport or protocol exceptions occurs
     */
    public WebResponse getResponse(String url, String expectedResponseCharset) throws IOException;

    /**
     * Return {@link WebResponse} which is shell on http response for specified {@link WebRequest}
     *
     * @param webRequest {@link WebRequest} with details about http request
     * @return {@link WebResponse} which wraps http response of request specified by webRequest
     * @throws IOException if transport or protocol exceptions occurs
     */
    public WebResponse getResponse(WebRequest webRequest) throws IOException;

    /**
     * Return {@link WebResponse} which is shell on http response for specified {@link WebRequest}
     *
     * @param webRequest {@link WebRequest} with details about http request
     * @param expectedResponseCharset charset which is expected in text of response
     * ISO-8859-1 will be used as response content charset if it will be {@code null}
     * @return {@link WebResponse} which wraps http response of request specified by webRequest
     * @throws IOException if transport or protocol exceptions occurs
     */
    public WebResponse getResponse(WebRequest webRequest, String expectedResponseCharset) throws IOException;

    /**
     * Return all http headers which are sended with every http request
     *
     * @return {@link Map<String, String>} with (name, value) pairs of http headers
     */
    public Map<String, String> getHeaders();

    /**
     * Return value of headr with specified name
     *
     * @param headerName name of the header
     * @return value of header with specified name
     */
    public String getHeader(String headerName);

    /**
     * Add headers to web browser so they will be sended with every http request
     * of instance of web browser
     *
     * @param headers headers to add
     */
    public WebBrowser addHeaders(Map<String, String> headers);

    /**
     * Add header to web browser specified by name and value so it
     * will be sended with every http request
     * of instance of web browser
     *
     * @param name  name of the http header
     * @param value value of the http header
     */
    public WebBrowser addHeader(String name, String value);

    /**
     * Used for initializing default headers of browser
     * from map
     *
     * @param defaultHeaders source map for default headers
     */
    public WebBrowser setDefaultHeaders(Map defaultHeaders);

    /**
     * Return count of attempts which web browser will undertake to get response
     *
     * @return count of attempts which web browser will undertake to get response
     */
    public Integer getRetryCount();

    /**
     * Set count of attempts which web browser will undertake to get response
     *
     * @param retryCount count of attempts which web browser will undertake to get response
     */
    public WebBrowser setRetryCount(Integer retryCount);

    /**
     * Return time which web browser will whait for every response
     *
     * @return time which web browser will whait for every responce
     */
    public Integer getSocketTimeout();

    /**
     * Set time which web browser will whait for every responce
     *
     * @param socketTimeout time which web browser will whait for every response
     */
    public WebBrowser setSocketTimeout(Integer socketTimeout);

    /**
     * Return time which web browser will whait for success connection
     * to remote host
     *
     * @return time which web browser will whait for success connection
     * to remote host
     */
    public Integer getConnectionTimeout();

    /**
     * Set time which web browser will whait for success connection
     * to remote host
     *
     * @param connectionTimeout time which web browser will whait for success connection
     * to remote host
     */
    public WebBrowser setConnectionTimeout(Integer connectionTimeout);

    /**
     * Return list of cookies which are will be sended with every http request
     *
     * @return list of cookies which are will be sended with every http request
     */
    public List<Cookie> getCookies();

    /**
     * Adds {@link Cookie} which will be sended with every http request of
     * web browser instance
     *
     * @param cookie {@link Cookie} to add
     */
    public WebBrowser addCookie(Cookie cookie);

    /**
     * Adds list of {@link Cookie} which will be sended with every http request of
     * web browser instance
     *
     * @param cookies list of cookies to be added
     */
    public WebBrowser addCookies(List<Cookie> cookies);

    /**
     * Remove all cookies from web browser, so
     * it will not send any cookie until it will be sended manually
     * or by some http response
     */
    public WebBrowser clearAllCookies();

    /**
     * Set up proxy which web browser will use for connection
     * @param url url of the proxy server
     * @param port port of the proxy server
     */
    public WebBrowser setProxy(String url, int port);

    /**
     * Tells web browser not to use proxy
     */
    public WebBrowser clearProxy();

    /**
     * used to abort http request for current thread;
     * @throws IOException
     */
    public WebBrowser abort();

}
