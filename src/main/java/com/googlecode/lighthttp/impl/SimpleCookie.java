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

import com.googlecode.lighthttp.Cookie;

/**
 * Simple implementation of {@link Cookie}
 *
 * @author Sergey Pilukin
 * @since 10.05.2008 1:03:26
 */
public final class SimpleCookie implements Cookie {
    protected String domain;
    protected String name;
    protected String value;
    protected String path;

    /**
     * Default constructor
     */
    public SimpleCookie() {
    }

    /**
     * Constructor for initializing
     * domain, name, value and path.<br/>
     * path will be initialized with "/" by default
     *
     * @param domain see {@link com.googlecode.lighthttp.Cookie#setDomain(String)}
     * @param name   see {@link com.googlecode.lighthttp.Cookie#setName(String)}
     * @param value  see {@link com.googlecode.lighthttp.Cookie#setValue(String)}
     * @see com.googlecode.lighthttp.Cookie#setValue(String)
     * @see com.googlecode.lighthttp.Cookie#setName(String)
     * @see com.googlecode.lighthttp.Cookie#setDomain(String)
     * @see com.googlecode.lighthttp.Cookie#setPath(String)
     */
    public SimpleCookie(String domain, String name, String value) {
        this.domain = domain;
        this.name = name;
        this.value = value;
        this.path = "/";
    }

    /**
     * {@inheritDoc}
     */
    public String getDomain() {
        return domain;
    }

    /**
     * {@inheritDoc}
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    public void setPath(String path) {
        this.path = path;
    }
}
