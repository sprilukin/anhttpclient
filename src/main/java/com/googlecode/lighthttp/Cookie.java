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

package com.googlecode.lighthttp;

/**
 * Used as simple wrapper under HTTP Cookie
 *
 * @author Sergey Pilukin
 * @version $Id$
 */
public interface Cookie {
    /**
     * Return url of domain for which this
     * cookie should be sent to server
     *
     * @return url of domain
     */
    public String getDomain();

    /**
     * Sets url of domain for which this
     * cookie should be sent to server
     *
     * @param domain url of the domain
     */
    public void setDomain(String domain);

    /**
     * Returns name of this cookie
     *
     * @return name of this cookie
     */
    public String getName();

    /**
     * Set name of this cookie
     *
     * @param name name of this cookie
     */
    public void setName(String name);

    /**
     * Return value of this cookie
     *
     * @return value of this cookie
     */
    public String getValue();

    /**
     * Sets value of this cookie
     *
     * @param value value of this cookie
     */
    public void setValue(String value);

    /**
     * Return path within domain for which this cookie should be sent.
     * For example if domain is:
     * <pre>
     *      http://somedomain.com
     * </pre>
     * and path equals to
     * <pre>
     *      /somepath
     * </pre>
     * then cookie will be sent for all request which url starts from<br/>
     * <pre>
     *      http://somedomain.com/somepath
     * </pre>
     *
     * @return path within domain for which this cookie should be sent.
     */
    public String getPath();

    /**
     * Sets path within domain for which this cookie should be sent.
     * See {@link #getPath()}
     *
     * @param path path within domain for which this cookie should be sent
     */
    public void setPath(String path);
}
