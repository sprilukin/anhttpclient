package com.googlecode.lighthttp.impl;

import com.googlecode.lighthttp.HttpConstants;
import com.googlecode.lighthttp.RequestMethod;
import com.googlecode.lighthttp.WebRequest;
import com.googlecode.lighthttp.WebResponse;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over HTTP GET request
 *
 * @author Sergey Prilukin
 * @since 08.04.2008 18:16:09
 */
public final class HttpGetWebRequest implements WebRequest {
    public static final String QUERY_SIGN = "?";
    public static final String ESCAPED_QUERY_SIGN = "\\?";
    public static final String AMPERSAND_SIGN = "&";
    public static final String EQUALS_SIGN = "=";

    protected String url;
    protected Map<String, String> headers = new HashMap<String, String>();

    public HttpGetWebRequest() {
        /* Default constructor */
    }

    /**
     * request url should be initialized in constructor
     *
     * @param url url for request
     */
    public HttpGetWebRequest(String url) {
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
     * <br/>
     * Because this is GET request parameters should be putted to url
     *
     * @return collection of request params from request url
     */
    public Map<String, String> getRequestParams() {
        String[] urlWithParamsArray = url.split(ESCAPED_QUERY_SIGN);

        if (urlWithParamsArray.length < 2) {
            return null;
        }

        String paramsString = url.split(ESCAPED_QUERY_SIGN)[1];
        String paramsArray[] = paramsString.split(AMPERSAND_SIGN);
        Map<String, String> paramsMap = new HashMap<String, String>(paramsArray.length);

        for (String paramsEntry : paramsArray) {
            String paramName = paramsEntry.split(EQUALS_SIGN)[0];
            String paramValue = paramsEntry.split(EQUALS_SIGN)[1];
            paramsMap.put(paramName, paramValue);
        }

        return paramsMap;
    }

    /**
     * {@inheritDoc}
     * <br/>
     * because this is GET request we should add params to request URL
     *
     * @param requestParams collection of (name, value) pairs of request params
     */
    public WebRequest addParams(Map<String, String> requestParams) {
        StringBuilder urlWithParams = new StringBuilder();
        urlWithParams.append(url);

        if (url.indexOf(QUERY_SIGN) == 0) {
            urlWithParams.append(QUERY_SIGN);
        }

        for (Map.Entry<String, String> requestParam : requestParams.entrySet()) {
            if (!urlWithParams.toString().endsWith(QUERY_SIGN)) {
                urlWithParams.append(AMPERSAND_SIGN);
            }

            urlWithParams.append(requestParam.getKey());
            urlWithParams.append(EQUALS_SIGN);
            urlWithParams.append(requestParam.getValue());
        }

        url = urlWithParams.toString();

        return this;
    }

    /**
     * {@inheritDoc}
     * <br/>
     * because this is GET request we should add parameter to request URL
     *
     * @param name  name of request parameter
     * @param value value of request parameter
     */
    public WebRequest addParam(String name, String value) {
        Map<String, String> singleParam = new HashMap<String, String>(1);

        singleParam.put(name, value);
        addParams(singleParam);

        return this;
    }

    /**
     * {@inheritDoc}
     * <br/>
     *
     * @return {@link RequestMethod#GET} because this is implementation of HTTP GET request
     */
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
