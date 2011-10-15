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

package anhttp;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Interface for creating and using easy http client
 * with minimum necessary methods
 *
 * @author Sergey Prilukin
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
     * Return all http headers which are sent with every http request
     *
     * @return {@code Map} with (name, value) pairs of http headers
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
     * Add headers to web browser so they will be sent with every http request
     * of instance of web browser
     *
     * @param headers headers to add
     */
    public void addHeaders(Map<String, String> headers);

    /**
     * Add header to web browser specified by name and value so it
     * will be sent with every http request
     * of instance of web browser
     *
     * @param name  name of the http header
     * @param value value of the http header
     */
    public void addHeader(String name, String value);

    /**
     * Used for initializing default headers of browser
     * from map
     *
     * @param defaultHeaders source map for default headers
     */
    public void setDefaultHeaders(Map<String, String> defaultHeaders);

    /**
     * Used for initializing default headers of browser
     * from map
     *
     * @param defaultHeaders {@link java.util.Properties} which contains default properties
     */
    public void setDefaultHeaders(Properties defaultHeaders);

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
    public void setRetryCount(Integer retryCount);

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
    public void setSocketTimeout(Integer socketTimeout);

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
    public void setConnectionTimeout(Integer connectionTimeout);

    /**
     * Return list of cookies which are will be sent with every http request
     *
     * @return list of cookies which are will be sent with every http request
     */
    public List<Cookie> getCookies();

    /**
     * Return cookie which matches passed name
     *
     * @param name name of the cookie to find
     * @return {@link Cookie} which matches passed name parameter
     */
    public Cookie getCookieByName(String name);

    /**
     * Adds {@link Cookie} which will be sent with every http request of
     * web browser instance
     *
     * @param cookie {@link Cookie} to add
     */
    public void addCookie(Cookie cookie);

    /**
     * Adds list of {@link Cookie} which will be sent with every http request of
     * web browser instance
     *
     * @param cookies list of cookies to be added
     */
    public void addCookies(List<Cookie> cookies);

    /**
     * Remove all cookies from web browser, so
     * it will not send any cookie until it will be sent manually
     * or by some http response
     */
    public void clearAllCookies();

    /**
     * Set up proxy which web browser will use for connection
     * @param url url of the proxy server
     * @param port port of the proxy server
     */
    public void setProxy(String url, int port);

    /**
     * Tells web browser not to use proxy
     */
    public void clearProxy();

    /**
     * used to abort http request for current thread;
     */
    public void abort();

}
