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

import com.googlecode.lighthttp.EntityEnclosingWebRequest;
import com.googlecode.lighthttp.RequestMethod;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper over HTTP POST request
 *
 * @author Sergey Pilukin
 * @version $Id$
 */
public class HttpPostWebRequest extends HttpGetWebRequest implements EntityEnclosingWebRequest {
    public static String OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    public static String TEXT_PLAIN_MIME_TYPE = "text/plain";

    protected String formParamsCharset = HTTP.UTF_8;
    protected Map<String, String> formParams = new HashMap<String, String>();
    Map<String, ContentBody> parts = new HashMap<String, ContentBody>();

    private String getMimeTypeOrDefault(String mimeType) {
        return mimeType != null ? mimeType : OCTET_STREAM_MIME_TYPE;
    }

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
    public Map<String, String> getFormParams() {
        return Collections.unmodifiableMap(formParams);
    }

    /**
     * {@inheritDoc}
     */
    public void addFormParams(Map<String, String> requestParams) {
        addFormParams(requestParams, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addFormParams(Map<String, String> requestParams, String charset) {
        parts.clear();
        formParamsCharset = charset != null ? charset : HTTP.UTF_8;
        formParams.putAll(requestParams);
    }

    /**
     * {@inheritDoc}
     */
    public void addFormParam(String name, String value) {
        addFormParam(name, value, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addFormParam(String name, String value, String charset) {
        parts.clear();
        formParamsCharset = charset != null ? charset : HTTP.UTF_8;
        formParams.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public String getFormParamsCharset() {
        return formParamsCharset;
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, File file, String mimeType, String charset, String name) throws IOException {
        parts.put(partName, new FileBody(file, name, getMimeTypeOrDefault(mimeType), charset));
        formParams.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, File file, String mimeType, String charset) throws IOException {
        addPart(partName, file, mimeType, charset, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, File file, String mimeType) throws IOException {
        addPart(partName, file, mimeType, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, File file) throws IOException {
        addPart(partName, file, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, InputStream inputStream, String name, String mimeType) throws IOException {
        parts.put(partName, new InputStreamBody(inputStream, getMimeTypeOrDefault(mimeType), name));
        formParams.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, InputStream inputStream, String mimeType) throws IOException {
        addPart(partName, inputStream, mimeType, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, InputStream inputStream) throws IOException {
        addPart(partName, inputStream, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, String string, String charset) {
        parts.put(partName, StringBody.create(string, TEXT_PLAIN_MIME_TYPE, charset != null ? Charset.forName(charset) : null));
        formParams.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, String string) {
        addPart(partName, string, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, byte[] byteArray, String mimeType, String name) {
        parts.put(partName, new ByteArrayBody(byteArray, getMimeTypeOrDefault(mimeType), name));
        formParams.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, byte[] byteArray, String mimeType) {
        addPart(partName, byteArray, mimeType, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addPart(String partName, byte[] byteArray) {
        addPart(partName, byteArray, null, null);
    }

    public Map<String, ContentBody> getParts() {
        return Collections.unmodifiableMap(parts);
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
