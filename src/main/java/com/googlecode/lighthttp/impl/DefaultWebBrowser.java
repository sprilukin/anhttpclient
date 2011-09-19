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
import com.googlecode.lighthttp.HttpConstants;
import com.googlecode.lighthttp.WebBrowser;
import com.googlecode.lighthttp.WebRequest;
import com.googlecode.lighthttp.WebResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Default implementation of {@link WebBrowser}
 * Has such configureable parameters:
 * - default headers    : Collection of HTTP headers which will be sent with every http request
 * - retry count        : count of repeating http request if previous one was unsuccessful
 * - connection timeout : time in milliseconds which determine connection timeout of HTTP request
 * - socket timeout     : time in milliseconds which determine socket timeout of HTTP request
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DefaultWebBrowser implements WebBrowser {
    public static final Log log = LogFactory.getLog(DefaultWebBrowser.class);

    protected HttpClient httpClient;
    protected CookieStore cookieStore = new BasicCookieStore();
    protected Map<String, String> defaultHeaders = new HashMap<String, String>();
    protected int retryCount = WebBrowserConstants.DEFAULT_RETRY_COUNT;
    protected int socketTimeout = WebBrowserConstants.DEFAULT_SOCKET_TIMEOUT;
    protected int connectionTimeout = WebBrowserConstants.DEFAULT_CONNECTION_TIMEOUT;
    protected ThreadLocal<HttpRequestBase> httpRequest = new ThreadLocal<HttpRequestBase>();

    private HttpParams httpParams;
    private String clientConnectionFactoryClassName = WebBrowserConstants.DEFAULT_CLIENT_CONNECTION_FACTORY_CLASS_NAME;
    private boolean threadSafe = false;
    private boolean initialized = false;


    static class GzipDecompressingEntity extends HttpEntityWrapper {
       public GzipDecompressingEntity(final HttpEntity entity) {
          super(entity);
       }

       @Override
       public InputStream getContent() throws IOException, IllegalStateException {
          // the wrapped entity's getContent() decides about repeatability
          InputStream wrappedin = wrappedEntity.getContent();
          return new GZIPInputStream(wrappedin);
       }

       @Override
       public long getContentLength() {
          // length of ungzipped content is not known
          return -1;
       }
    }

    /**
     * Allows to set httpClient implementation directly
     * @param httpClient instance of {@link HttpClient}
     */
    public void setHttpClient(HttpClient httpClient) {
        synchronized (this.getClass()) {
            this.httpClient = httpClient;
            this.initialized = false;
        }
    }

    /**
     * Allows to set {@link HttpParams}
     * Will take effect only if httpClient is initialized inside DefaultWebBrowser
     * @param httpParams {@HttpParams} to set.
     */
    public void setHttpParams(HttpParams httpParams) {
        this.httpParams = httpParams;
        if (this.httpParams != null && this.httpParams.getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME) == null) {
            this.httpParams.setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME, clientConnectionFactoryClassName);
        }

        if (httpClient != null && httpClient instanceof DefaultHttpClient) {
            synchronized (this.getClass()) {
                httpClient = null;
                this.initialized = false;
            }
        }
    }

    private HttpParams getBasicHttpParams() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
        params.setParameter(CoreProtocolPNames.USER_AGENT, WebBrowserConstants.DEFAULT_USER_AGENT);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
        params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        params.setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME, clientConnectionFactoryClassName);

        /*Custom parameter to be used in implementation of {@link ClientConnectionManagerFactory}*/
        params.setParameter(ClientConnectionManagerFactoryImpl.THREAD_SAFE_CONNECTION_MANAGER, this.threadSafe);

        return params;
    }

    private void addGZIPResponseInterceptor(HttpClient httpClient) {
        if (AbstractHttpClient.class.isAssignableFrom(httpClient.getClass())) {
            ((AbstractHttpClient)httpClient).addResponseInterceptor(new HttpResponseInterceptor() {
                public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    Header contentEncodingHeader = entity.getContentEncoding();
                    if (contentEncodingHeader != null) {
                        HeaderElement[] codecs = contentEncodingHeader.getElements();
                        for (HeaderElement codec : codecs) {
                            if (codec.getName().equalsIgnoreCase(HttpConstants.GZIP)) {
                                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Default constructor
     * apache http client will initialized here as not thread safe http client
     */
    public DefaultWebBrowser() {
        this(false);
    }

    /**
     * Constructor which allows to specify if webBrowser should be thread Safe
     * @param threadSafe if {@code true} then webBrowser will be thread safe
     * and not thread safe - if {@code false}
     */
    public DefaultWebBrowser(boolean threadSafe) {
        this.threadSafe = threadSafe;
    }

    /**
     * Initialize new instance of httpClient
     */
    private void initHttpClient() {
        if (!this.initialized) {
            synchronized (this.getClass()) {
                if (!this.initialized) {
                    if (httpClient == null) {
                        if (httpParams == null) {
                            httpParams = getBasicHttpParams();
                        }

                        httpClient = new DefaultHttpClient(null, getBasicHttpParams());
                        addGZIPResponseInterceptor(httpClient);
                    }

                    this.initialized = true;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser setDefaultHeaders(final Map defaultHeaders) {
        this.defaultHeaders.clear();
        for (Object entryObject : defaultHeaders.entrySet()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)entryObject;
            String headerName = String.valueOf(entry.getKey());
            String headerValue = String.valueOf(entry.getValue());
            this.addHeader(headerName, headerValue);
        }

        return this;
    }

    /**
     * Makes default initialization of HttpMethodBase before any request
     * such as cookie policy, retrycount, timeout
     *
     * @param httpMethodBase {@link HttpRequestBase} for making default initialization
     */
    private void setDefaultMethodParams(final HttpRequestBase httpMethodBase) {
        httpMethodBase.getParams().setBooleanParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true);
        httpMethodBase.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);


        // We use here DefaultHttpMethodRetryHandler with <b>true</b> parameter
        // because we suppose that if method was successfully sent its headers
        // it could also be retried
        if (AbstractHttpClient.class.isAssignableFrom(httpClient.getClass())) {
            ((AbstractHttpClient)httpClient).setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, true));
        }
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
    }

    /**
     * Add default headers to web request and
     * then add specific headers for current request
     *
     * @param httpMethodBase http method for adding headers
     * @param methodHeaders  headers specific for current request
     */
    private void setHeaders(final HttpRequestBase httpMethodBase, final Map<String, String> methodHeaders) {

        //set default headers
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            httpMethodBase.setHeader(entry.getKey(), entry.getValue());
        }

        //set method headers
        for (Map.Entry<String, String> entry : methodHeaders.entrySet()) {
            httpMethodBase.setHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Execute specified request
     *
     * @param httpUriRequest request to execute
     * @return code of http response
     * @throws java.io.IOException if errors occurs during request
     * @throws java.net.SocketTimeoutException if timeout occurs see params
     *          settings in {@link #setDefaultMethodParams} method
     */
    private HttpResponse executeMethod(HttpUriRequest httpUriRequest) throws IOException {
        if (log.isDebugEnabled()) {
            for (Header header: httpUriRequest.getAllHeaders()) {
                log.debug(String.format("LIGHTHTTP REQUEST HEADER: [%s: %s]", header.getName(), header.getValue()));
            }
        }

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return httpClient.execute(httpUriRequest, localContext);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(String url) throws IOException {
        return getResponse(url, null);
    }

    public WebResponse getResponse(String url, String expectedResponseCharset) throws IOException {
        WebRequest req = new HttpGetWebRequest(url);

        return getResponse(req, expectedResponseCharset);
    }

    /**
     * Return {@link HttpRequestBase} from WebRequest which is
     * shell on http GET or DELETE request
     *
     * @param webRequest shell under http GET request
     * @param httpRequest HttpRequestBase instance
     * @return HttpMethodBase for specified shell on http GET request
     */
    private HttpRequestBase populateHttpRequestBaseMethod(WebRequest webRequest, HttpRequestBase httpRequest) {
        setDefaultMethodParams(httpRequest);
        setHeaders(httpRequest, webRequest.getHeaders());

        return httpRequest;
    }

    /**
     * Return {@link HttpRequestBase} from WebRequest which is
     * shell on http POST or PUT request
     *
     * @param webRequest shell under http POST request
     * @param httpRequest HttpEntityEnclosingRequestBase instance
     * @return HttpMethodBase for specified shell on http POST request
     */
    private HttpRequestBase populateHttpEntityEnclosingRequestBaseMethod(
            WebRequest webRequest,
            HttpEntityEnclosingRequestBase httpRequest) {

        setDefaultMethodParams(httpRequest);
        setHeaders(httpRequest, webRequest.getHeaders());
        httpRequest.addHeader(HTTP.CONTENT_TYPE, HttpConstants.MIME_FORM_ENCODED);

        // data - name/value params
        List<NameValuePair> nameValuePairList = null;
        Map<String, String> requestParams = webRequest.getRequestParams();
        if ((requestParams != null) && (requestParams.size() > 0)) {
            nameValuePairList = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        if (nameValuePairList != null) {
            try {
                httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairList, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Error peforming HTTP request: " + e.getMessage(), e);
            }
        }

        return httpRequest;
    }

    /**
     * If {@link HttpRequestBase} httpMethodBase has :location: header in response headers then
     * redirect will be perfirmed
     *
     * @param httpMethodBase {@link HttpRequestBase} with original request
     * @param charset charset of response text content
     * @return web response which is not needed in redirects
     * @throws java.io.IOException if errors occured during executing redirect
     */
    private WebResponse processResponse(HttpResponse response, HttpRequestBase httpMethodBase, String charset) throws IOException {
        if (log.isDebugEnabled()) {
            for (Header header: response.getAllHeaders()) {
                log.debug(String.format("LIGHTHTTP RESPONSE HEADER: [%s: %s]", header.getName(), header.getValue()));
            }
        }

        return new HttpWebResponse(response, httpMethodBase, charset);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        return getResponse(webRequest, null);
    }

    /**
     * {@inheritDoc}
     */
    public WebResponse getResponse(WebRequest webRequest, String charset) throws IOException {
        initHttpClient();

        switch (webRequest.getRequestMethod()) {
            case GET:
                httpRequest.set(populateHttpRequestBaseMethod(webRequest, new HttpGet(webRequest.getUrl())));
                break;
            case DELETE:
                httpRequest.set(populateHttpRequestBaseMethod(webRequest, new HttpDelete(webRequest.getUrl())));
                break;
            case POST:
                httpRequest.set(populateHttpEntityEnclosingRequestBaseMethod(webRequest, new HttpPost(webRequest.getUrl())));
                break;
            case PUT:
                httpRequest.set(populateHttpEntityEnclosingRequestBaseMethod(webRequest, new HttpPut(webRequest.getUrl())));
                break;
            default:
                throw new RuntimeException("Method not yet supported: " + webRequest.getRequestMethod());
        }

        WebResponse resp;

        HttpResponse response = executeMethod(httpRequest.get());
        if (response == null) {
            throw new IOException("LIGHTHTTP: An empty response received from server. Possible reason: host is offline");
        }

        resp = processResponse(response, httpRequest.get(), charset);
        if (log.isDebugEnabled()) {
            log.debug(String.format("LIGHTHTTP REQUEST COMPLETED. SIZE: %s", resp.getBytes().length));
        }

        return resp;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(defaultHeaders);
    }

    /**
     * {@inheritDoc}
     */
    public String getHeader(String headerName) {
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(headerName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser addHeaders(Map<String, String> headers) {
        defaultHeaders.putAll(headers);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser addHeader(String name, String value) {
        defaultHeaders.put(name, value);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getRetryCount() {
        return retryCount;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser setConnectionTimeout(Integer connectionTimeout) {
        initHttpClient();
        this.connectionTimeout = connectionTimeout;
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<Cookie>();
        for (org.apache.http.cookie.Cookie apacheCookie : cookieStore.getCookies()) {
            Cookie cookie = new SimpleCookie();
            cookie.setDomain(apacheCookie.getDomain());
            cookie.setName(apacheCookie.getName());
            cookie.setValue(apacheCookie.getValue());
            cookie.setPath(apacheCookie.getPath());
            cookies.add(cookie);
        }

        return cookies;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser addCookie(Cookie cookie) {
        BasicClientCookie apacheCookie =
                new BasicClientCookie(
                        cookie.getName(), cookie.getValue()
                );
        apacheCookie.setPath(cookie.getPath());
        apacheCookie.setDomain(cookie.getDomain());
        cookieStore.addCookie(apacheCookie);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser addCookies(List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            BasicClientCookie apacheCookie =
                    new BasicClientCookie(cookie.getName(), cookie.getValue());
            apacheCookie.setPath(cookie.getPath());
            apacheCookie.setDomain(cookie.getDomain());
            cookieStore.addCookie(apacheCookie);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser clearAllCookies() {
        cookieStore.clear();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser setProxy(String url, int port) {
        initHttpClient();
        HttpHost proxy = new HttpHost(url, port);
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser clearProxy() {
        initHttpClient();
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebBrowser abort() {
        if (httpRequest.get() != null && !httpRequest.get().isAborted()) {
            httpRequest.get().abort();
        }

        return this;
    }
}
