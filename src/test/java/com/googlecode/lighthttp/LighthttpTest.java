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
import org.apache.http.protocol.HTTP;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

/**
 * Tests http client
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class LighthttpTest {

    private WebBrowser wb = new DefaultWebBrowser();
    private Properties defaultHeaders = new Properties();

    @Before
    public void initialize() throws Exception {
        InputStream headersAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/googlecode/lighthttp/defaultheaders.properties");

        defaultHeaders.load(headersAsStream);

        wb.setDefaultHeaders(defaultHeaders);
        wb.setSocketTimeout(500);
    }

    @Test
    public void testPostRequest() throws Exception {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", "sss@ggg.com");
        params.put("space", "aaa bbb");
        params.put("russian", "привет");
        params.put("some_chars", "!@#$%^&*()_+|");


        SimpleHttpServer server = new DefaultSimpleHttpServer();
        server.addHandler("/post", new BaseSimpleHttpHandler() {
            @Override
            protected byte[] getResponse(HttpExcahngeFacade httpExcahngeFacade) {
                assertEquals("Method should be POST", "POST", httpExcahngeFacade.getRequestMethod());
                assertEquals("Form should be url-encoded",
                        HttpConstants.MIME_FORM_ENCODED,
                        httpExcahngeFacade.getRequestHeaders().get(HTTP.CONTENT_TYPE).get(0));

                try {
                    String postParams = new String(httpExcahngeFacade.getRequestBody());
                    String[] paramValuePairs = postParams.split("\\&");
                    for (String paramValuePair: paramValuePairs) {
                        String[] paramValueArray = paramValuePair.split("\\=");
                        String param = paramValueArray[0];
                        String value = java.net.URLDecoder.decode(paramValueArray[1], "UTF-8");
                        assertEquals(String.format("incorrect param value: %s   should be: %s", value, params.get(param)), value, params.get(param));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        }).start();

        WebRequest req = new HttpPostWebRequest(server.getBaseUrl() + "/post");
        req.addParams(params);

        wb.getResponse(req);
        server.stop();
    }

    @Test
    public void testGetRequest() throws Exception {

        final String responseText = "Hello from SimpleHttperver";

        final SimpleHttpServer server = new DefaultSimpleHttpServer();
        server.addHandler("/get", new BaseSimpleHttpHandler() {
            @Override
            protected byte[] getResponse(HttpExcahngeFacade httpExcahngeFacade) {
                assertEquals("Method should be GET", "GET", httpExcahngeFacade.getRequestMethod());
                assertEquals("Count of request headers should be equal to size of default headers plus additional Host header",
                        defaultHeaders.size(), httpExcahngeFacade.getRequestHeaders().size() - 1);
                assertEquals(httpExcahngeFacade.getRequestHeaders().get("Host").get(0), DefaultSimpleHttpServer.DEFAULT_HOST + ":" + server.getPort());
                for (Map.Entry<String, List<String>> entry: httpExcahngeFacade.getRequestHeaders().entrySet()) {
                    if (!"Host".equals(entry.getKey())) {
                        assertEquals(String.format("sent header [%s] not equals to received", entry.getKey()),
                                defaultHeaders.get(entry.getKey()), entry.getValue().get(0));
                    }
                }
                return responseText.getBytes();
            }
        }).start();

        WebRequest req = new HttpGetWebRequest(server.getBaseUrl() + "/get?param1=value1");
        WebResponse resp = wb.getResponse(req);
        server.stop();
        assertEquals("Response from server is incorrect", responseText, resp.getText());
    }

    @Test
    public void testGzipResponse() throws Exception {

        final String responseText = "Hello from SimpleHttperver";

        SimpleHttpServer server = new DefaultSimpleHttpServer();
        server.addHandler("/gzip", new BaseSimpleHttpHandler() {
            @Override
            protected byte[] getResponse(HttpExcahngeFacade httpExcahngeFacade) {
                setResponseHeader("Content-Encoding", "gzip");
                setResponseHeader("Content-length", String.valueOf(responseText.length()));

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    OutputStream os = new GZIPOutputStream(baos);
                    os.write(responseText.getBytes());
                    os.flush();
                    os.close();
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        WebRequest req = new HttpGetWebRequest(server.getBaseUrl() + "/gzip");
        WebResponse resp = wb.getResponse(req);
        server.stop();
        assertEquals("Response from server is incorrect", responseText, resp.getText());
    }
}
