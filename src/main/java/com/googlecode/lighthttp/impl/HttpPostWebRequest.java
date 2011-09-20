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

import com.googlecode.lighthttp.RequestMethod;
import com.googlecode.lighthttp.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over HTTP POST request
 *
 * @author Sergey Pilukin
 * @version $Id$
 */
public class HttpPostWebRequest extends HttpGetWebRequest {
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
    public void addParams(Map<String, String> requestParams) {
        params.putAll(requestParams);
    }

    /**
     * {@inheritDoc}
     */
    public void addParam(String name, String value) {
        params.put(name, value);
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
