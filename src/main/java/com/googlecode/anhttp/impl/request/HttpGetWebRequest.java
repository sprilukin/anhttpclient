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

package com.googlecode.anhttp.impl.request;

import com.googlecode.anhttp.HttpConstants;
import com.googlecode.anhttp.RequestMethod;
import com.googlecode.anhttp.WebRequest;
import com.googlecode.anhttp.WebResponse;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over HTTP GET request
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class HttpGetWebRequest implements WebRequest {
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
     * request url can be initialized in constructor
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

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * {@inheritDoc}
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
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
    public void setReferer(String referer) {
        headers.put(HttpConstants.REFERER_HEADER, referer);
    }

    /**
     * {@inheritDoc}
     */
    public void setReferer(WebResponse response)  throws MalformedURLException {
        headers.put(HttpConstants.REFERER_HEADER, response.getUrl().toString());
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
            return Collections.emptyMap();
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
    public void addParams(Map<String, String> requestParams) {
        StringBuilder urlWithParams = new StringBuilder();
        urlWithParams.append(url);

        if (!url.contains(QUERY_SIGN)) {
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
    }

    /**
     * {@inheritDoc}
     * <br/>
     * because this is GET request we should add parameter to request URL
     *
     * @param name  name of request parameter
     * @param value value of request parameter
     */
    public void addParam(String name, String value) {
        Map<String, String> singleParam = new HashMap<String, String>(1);

        singleParam.put(name, value);
        addParams(singleParam);
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
