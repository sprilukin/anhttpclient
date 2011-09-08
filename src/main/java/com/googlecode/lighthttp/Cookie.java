package com.googlecode.lighthttp;

/**
 * Used as simple wrapper under HTTP Cookie
 *
 * @author Sergey Pilukin
 * @since 10.05.2008 1:01:05
 */
public interface Cookie {
    /**
     * Return url of domain for which this
     * cookie should be sended to server
     *
     * @return url of domain
     */
    public String getDomain();

    /**
     * Sets url of domain for which this
     * cookie should be sended to server
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
     * Return path within domain for which this cookie should be sended.
     * For example if domain is:
     * <pre>
     *      http://somedomain.com
     * </pre>
     * and path equals to
     * <pre>
     *      /somepath
     * </pre>
     * then cookie will be sended for all request which url starts from<br/>
     * <pre>
     *      http://somedomain.com/somepath
     * </pre>
     *
     * @return path within domain for which this cookie should be sended.
     */
    public String getPath();

    /**
     * Sets path within domain for which this cookie should be sended.
     * See {@link #getPath()}
     *
     * @param path path within domain for which this cookie should be sended
     */
    public void setPath(String path);
}
