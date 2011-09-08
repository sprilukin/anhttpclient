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
 * @since 29.05.2008 10:02:10
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
