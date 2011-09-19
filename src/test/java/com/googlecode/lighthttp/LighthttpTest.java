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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.googlecode.lighthttp.impl.DefaultWebBrowser;
import com.googlecode.lighthttp.impl.HttpGetWebRequest;
import com.googlecode.lighthttp.impl.HttpPostWebRequest;
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

    @Test
    public void testHttpClient() {
        WebRequest req = new HttpGetWebRequest("http://google.com");
        WebResponse resp = null;
        try {
            resp = wb.getResponse(req);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        System.out.println("===== HEADRS ========");
        for (String header : resp.getHeaders().keySet()) {
            System.out.println(header + ": " + resp.getHeader(header));
        }

        System.out.println("\n===== RESPONSE ========");
        try {
            System.out.println(resp.getText());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail();            
        }

        assertNotNull(resp);
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
}
