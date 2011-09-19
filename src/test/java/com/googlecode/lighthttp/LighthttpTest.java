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

import static org.junit.Assert.assertEquals;

import com.googlecode.lighthttp.impl.DefaultWebBrowser;
import com.googlecode.lighthttp.impl.HttpGetWebRequest;
import com.googlecode.lighthttp.impl.HttpPostWebRequest;
import com.googlecode.lighthttp.server.BaseSimpleHttpHandler;
import com.googlecode.lighthttp.server.DefaultSimpleHttpServer;
import com.googlecode.lighthttp.server.HttpExcahngeFacade;
import com.googlecode.lighthttp.server.SimpleHttpServer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Tests http client
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class LighthttpTest {

    private WebBrowser wb = new DefaultWebBrowser();

    @Before
    public void initialize() throws Exception {
        InputStream headersAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/googlecode/lighthttp/defaultheaders.properties");

        Properties defaultHeaders = new Properties();
        defaultHeaders.load(headersAsStream);

        wb.setDefaultHeaders(defaultHeaders);
    }

    @Ignore
    public void testEmailInForm() throws Exception {
        WebRequest req = new HttpPostWebRequest("http://google.com");
        req.addParam("email", "sss@ggg.com");
        req.addParam("space", "aaa bbb");
        req.addParam("russian", "привет");
        req.addParam("some_chars", "!@#$%^&*()_+|");

        WebResponse resp = wb.getResponse(req);
    }

    @Test
    public void testGetRequest() throws Exception {

        final String responseText = "Hello from SimpleHttperver";

        SimpleHttpServer server = new DefaultSimpleHttpServer();
        server.addHandler("/index/path", new BaseSimpleHttpHandler() {
            @Override
            protected byte[] getResponse(HttpExcahngeFacade httpExcahngeFacade) {
                assertEquals("Method should be GET", "GET", httpExcahngeFacade.getRequestMethod());
                return responseText.getBytes();
            }
        }).start();

        WebRequest req = new HttpGetWebRequest(server.getBaseUrl() + "/index/path?param1=value1");
        WebResponse resp = wb.getResponse(req);
        server.stop();
        assertEquals("Response from server is incorrect", responseText, resp.getText());
    }
}
