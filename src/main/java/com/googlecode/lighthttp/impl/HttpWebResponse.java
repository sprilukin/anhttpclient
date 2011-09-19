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

import com.googlecode.lighthttp.HttpConstants;
import com.googlecode.lighthttp.WebResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over http response
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public final class HttpWebResponse implements WebResponse {
    protected Map<String, String> responseHeaders = new HashMap<String, String>();
    protected int responseCode;
    protected byte[] responseBody;
    protected URL url;
    protected String responseBodyCharset = null;

    /**
     * Just copy response headers to internal responseHeaders map for
     * later using in {@link #getContentType}, {@link #getHeader}, {@link #getHeaders}
     *
     * @param response apache {@link HttpResponse} wich holds http response
     */
    private void setResponseHeaders(HttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            responseHeaders.put(header.getName(), header.getValue());
        }
    }

    /**
     * Get reponse body from apache {@link HttpResponse} wich holds http request
     * and sets it to internal byte array.
     *
     * @param httpResponse apache {@link HttpResponse} wich holds http response
     * @throws IOException if errors occured while getting original response as stream
     *                     or converting original input stream to byte array
     */
    private void setResponseBody(HttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        InputStream content = entity.getContent();
        try {
            responseBody = IOUtils.toByteArray(content);
        } finally {
            content.close();
        }
    }

    /**
     * Constructor. response code and original apache {@link HttpResponse} and {@link HttpRequestBase}
     * should be specified for wrapping http response
     *
     * @param httpReponse  http response to get request status code and request body
     * @param httpRequestBase original apache {@link HttpRequestBase} to get request URL
     * @param responseBodyCharset excpected charset of the response body
     */
    public HttpWebResponse(HttpResponse httpReponse, HttpRequestBase httpRequestBase, String responseBodyCharset) {
        this.responseBodyCharset = responseBodyCharset;
        this.responseCode = httpReponse.getStatusLine().getStatusCode();
        setResponseHeaders(httpReponse);
        try {
            url = new URL(httpRequestBase.getURI().toString());
            setResponseBody(httpReponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public URL getUrl() throws MalformedURLException {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() throws UnsupportedEncodingException {
        if (responseBodyCharset != null) {
            return new String(responseBody, responseBodyCharset);
        } else {
            return new String(responseBody);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType() {
        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            if (HTTP.CONTENT_TYPE.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeaders() {
        return responseHeaders;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeader(String headerName) {
        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(headerName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getBytes() {
        return responseBody;
    }

    /**
     * {@inheritDoc}
     */
    public int getResponseCode() {
        return responseCode;
    }
}
