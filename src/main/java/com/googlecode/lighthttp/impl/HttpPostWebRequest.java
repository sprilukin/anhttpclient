package com.googlecode.lighthttp.impl;

import com.googlecode.lighthttp.RequestMethod;
import com.googlecode.lighthttp.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over HTTP POST request
 *
 * @author Sergey Pilukin
 * @since 25.04.2008 14:35:45
 */
public final class HttpPostWebRequest extends HttpGetWebRequest {
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
    public Map<String, String> getRequestParams() {
        Map<String, String> allParams = new HashMap<String, String>(super.getRequestParams());
        allParams.putAll(params);
        return allParams;
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
