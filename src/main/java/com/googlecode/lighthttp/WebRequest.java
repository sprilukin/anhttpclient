package com.googlecode.lighthttp;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Used as easy wrapper under http request
 *
 * @author Sergey Prilukin
 * @since 08.04.2008 18:15:56
 */
public interface WebRequest {
    /**
     * Return url of http request
     *
     * @return url of http request
     */
    public String getUrl();

    public WebRequest setUrl(String url);

    /**
     * Add http headers which will be sended with this and only
     * this web request. This headers will be added to
     * collection of default headers of web browser which
     * will execute this request and will rewrite default headers which
     * has equals header name
     *
     * @param headers headers to add
     */
    public WebRequest addHeaders(Map<String, String> headers);

    /**
     * Add http header which will be sended with this and only
     * this web request. This header will be added to
     * collection of default headers of web browser which
     * will execute this request and will rewrite default header which
     * has header name equals to name of this header
     *
     * @param name  name of the request header
     * @param value value of the request header
     */
    public WebRequest addHeader(String name, String value);

    /**
     * Return collection of all headers which will be added to
     * default web browser headers during request
     *
     * @return map of headers
     */
    public Map<String, String> getHeaders();

    /**
     * Set "Referer" header to request
     * @param referer value of referer header
     */
    public WebRequest setReferer(String referer);

    /**
     * Set "Referer" header to request as url of
     * passed {@link jhttpcrowler.core.utils.httpclient.WebResponse}
     *
     * @param response response which url will serve as referer header
     *
     * @throws MalformedURLException if url of response param is malformed
     */
    public WebRequest setReferer(WebResponse response) throws MalformedURLException;

    /**
     * Return colleaction of (name, value) pairs of parameters which
     * will be sended to server when web browser will execute this web request.
     *
     * @return collection of request params
     */
    public Map<String, String> getRequestParams();

    /**
     * Add request params to this request, so they are will be sended to server
     * when web browser will execute this request
     *
     * @param requestParams collection of (name, value) pairs of request params
     */
    public WebRequest addParams(Map<String, String> requestParams);

    /**
     * Add request parameter to this request, so it will be sended to server
     * when web browser will execute this request
     *
     * @param name  name of requets parameter
     * @param value value of request parameter
     */
    public WebRequest addParam(String name, String value);

    /**
     * Return requets method:<br/>
     * {@link RequestMethod#GET} if this is HTTP GET request
     * or {@link RequestMethod#POST} if this is HTTP POST request
     *
     * @return method of this web request
     */
    public RequestMethod getRequestMethod();
}
