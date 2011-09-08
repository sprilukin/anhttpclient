package com.googlecode.lighthttp.impl;

import com.googlecode.lighthttp.HttpConstants;
import com.googlecode.lighthttp.RequestMethod;
import com.googlecode.lighthttp.WebRequest;
import com.googlecode.lighthttp.WebResponse;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper under HTTP POST request
 *
 * @author Sergey Pilukin
 * @since 25.04.2008 14:35:45
 */
public final class HttpPostWebRequest implements WebRequest {
    protected String url;
    protected Map<String, String> headers = new HashMap<String, String>();
    protected Map<String, String> params = new HashMap<String, String>();

    public HttpPostWebRequest() {
        /* Default constructor */
    }

    /**
     * request url should be initialized in constructor
     *
     * @param url url for request
     */
    public HttpPostWebRequest(String url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl() {
        return url;
    }

    public WebRequest setUrl(String url) {
        this.url = url;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest addHeader(String name, String value) {
        headers.put(name, value);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest setReferer(String referer) {
        headers.put(HttpConstants.REFERER_HEADER, referer);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest setReferer(WebResponse response)  throws MalformedURLException {
        headers.put(HttpConstants.REFERER_HEADER, response.getUrl().toString());

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getRequestParams() {
        return params;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest addParams(Map<String, String> requestParams) {
        params.putAll(requestParams);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public WebRequest addParam(String name, String value) {
        params.put(name, value);

        return this;
    }

    /**
     * {@inheritDoc}
     * <br/>
     *
     * @return {@link RequestMethod#POST} because this is implementation of HTTP POST request
     */
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
