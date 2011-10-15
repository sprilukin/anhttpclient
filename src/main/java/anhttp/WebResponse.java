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

package anhttp;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Wrapper under http response
 *
 * @author Sergey Prilukin
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
     * Return {@link Map&lt;String, String&gt;} of (name, value) pairs of headers of http response
     *
     * @return {@link Map&lt;String, String&gt;} of (name, value) pairs of headers of http response
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
