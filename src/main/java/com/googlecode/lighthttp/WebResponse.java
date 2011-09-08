package com.googlecode.lighthttp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Wrapper under http response
 *
 * @author Sergey Prilukin
 * @since 08.04.2008 17:22:32
 */
public interface WebResponse {

    /**
     * Return url from where this reponse was given
     *
     * @return {@link URL} from where this reponse was given
     * @throws MalformedURLException if url has malformed syntax
     */
    public URL getUrl() throws MalformedURLException;

    /**
     * Returns http response body as string. If {@link @setResponseCharset} was used
     * then this charset will be supposed as reponse charset. Else  default
     * platform charset will be used (UTF-8 ?)
     *
     * @return http response body as string
     * @throws UnsupportedEncodingException if {@link @setResponseCharset}
     *                                      was used and unsupposted encoding was setted
     */
    public String getText() throws UnsupportedEncodingException;

    /**
     * Return content type of the http response.
     * Actually it just returns value of "Content-Type" header
     * from http response. It could be looks like this:
     * <pre>
     *  	text/html; charset=UTF-8
     * </pre>
     *
     * @return string with content type of http response
     */
    public String getContentType();

    /**
     * Return {@link Map<String, String>} of (name, value) pairs of headers of http response
     *
     * @return {@link Map<String, String>} of (name, value) pairs of headers of http response
     */
    public Map<String, String> getHeaders();

    /**
     * Get http response header specified by header name
     *
     * @param headerName name of header
     * @return vlaue of the header
     */
    public String getHeader(String headerName);

    /**
     * Return byte array of http response body without any transformation
     * such as changing encoding etc.
     *
     * @return byte array with http response body
     */
    public byte[] getBytes();

    /**
     * Return code of http reponse which server returns as answer
     * on http request. Codes with numbers 3xx are not returned by default
     * because web browser automatically process redirects
     *
     * @return http response code
     */
    public int getResponseCode();
}
