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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.googlecode.lighthttp.impl.DefaultWebBrowser;
import com.googlecode.lighthttp.impl.HttpDeleteWebRequest;
import com.googlecode.lighthttp.impl.HttpGetWebRequest;
import com.googlecode.lighthttp.impl.HttpHeadWebRequest;
import com.googlecode.lighthttp.impl.HttpOptionsWebRequest;
import com.googlecode.lighthttp.impl.HttpPostWebRequest;
import com.googlecode.lighthttp.impl.HttpPutWebRequest;
import com.googlecode.lighthttp.impl.HttpTraceWebRequest;
import com.googlecode.lighthttp.server.SimpleHttpHandlerAdapter;
import com.googlecode.lighthttp.server.DefaultSimpleHttpServer;
import com.googlecode.lighthttp.server.HttpRequestContext;
import com.googlecode.lighthttp.server.SimpleHttpServer;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
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

    private WebBrowser wb;
    private Properties defaultHeaders;
    private Map<RequestMethod, Class<? extends WebRequest>> allRequests;
    private SimpleHttpServer server;

    @Before
    public void initialize() throws Exception {
        InputStream headersAsStream =
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("com/googlecode/lighthttp/defaultheaders.properties");

        defaultHeaders = new Properties();
        defaultHeaders.load(headersAsStream);

        wb = new DefaultWebBrowser();
        wb.setDefaultHeaders(defaultHeaders);
        wb.setSocketTimeout(100);

        allRequests = new HashMap<RequestMethod, Class<? extends WebRequest>>(7);
        allRequests.put(RequestMethod.GET, HttpGetWebRequest.class);
        allRequests.put(RequestMethod.POST, HttpPostWebRequest.class);
        allRequests.put(RequestMethod.DELETE, HttpDeleteWebRequest.class);
        allRequests.put(RequestMethod.HEAD, HttpHeadWebRequest.class);
        allRequests.put(RequestMethod.OPTIONS, HttpOptionsWebRequest.class);
        allRequests.put(RequestMethod.PUT, HttpPutWebRequest.class);
        allRequests.put(RequestMethod.TRACE, HttpTraceWebRequest.class);

        server = new DefaultSimpleHttpServer();
        server.start();
    }

    @After
    public void finish() {
        server.stop();
    }

    @Test
    public void testRequestMethodsAndDefaultHeadersAndHostAndResponseText() throws Exception {
        final String methodParamName = "method";
        final String responseText = "Hello from SimpleHttperver";

        server.addHandler("/index", new SimpleHttpHandlerAdapter() {
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                URI requestURI = httpRequestContext.getRequestURI();
                assertEquals(methodParamName, requestURI.getQuery().split("=")[0]);
                String method = requestURI.getQuery().split("=")[1];
                assertEquals("Method should be " + method, method, httpRequestContext.getRequestMethod());

                //In PUT and POST methods Content-length headers should be added
                int headersSize =
                        method.equals(RequestMethod.POST.toString())
                                || method.equals(RequestMethod.PUT.toString())
                                ? defaultHeaders.size() + 1
                                : defaultHeaders.size();

                assertEquals("Count of request headers is incorrect",
                        headersSize, httpRequestContext.getRequestHeaders().size() - 1);
                assertEquals("Host request header is unexpected",
                        httpRequestContext.getRequestHeaders().get("Host").get(0),
                        DefaultSimpleHttpServer.DEFAULT_HOST + ":" + server.getPort());

                for (Map.Entry<String, List<String>> entry: httpRequestContext.getRequestHeaders().entrySet()) {

                    if ("Content-length".equalsIgnoreCase(entry.getKey())) {
                        //assertEquals(httpRequestContext.getRequestBody().length,
                        //        Integer.valueOf(entry.getValue().get(0)).intValue());
                        continue;
                    } else if ("Host".equalsIgnoreCase(entry.getKey())) {
                        continue;
                    }

                    //Test default headers
                    assertEquals(String.format("sent header [%s] not equals to received one", entry.getKey()),
                            defaultHeaders.get(entry.getKey()), entry.getValue().get(0));
                }

                return responseText.getBytes();
            }
        });

        for (Map.Entry<RequestMethod, Class<? extends WebRequest>> entry: allRequests.entrySet()) {
            WebRequest req = entry.getValue().newInstance();
            req.setUrl(server.getBaseUrl() + "/index");
            req.addParam(methodParamName, entry.getKey().toString());
            WebResponse resp = wb.getResponse(req);
            if (!entry.getKey().equals(RequestMethod.HEAD)) {
                assertEquals("Response from server is incorrect", responseText, resp.getText());
            } else {
                assertNull(resp.getBytes());
            }
        }
    }

    @Test
    public void testFormParamsRequest() throws Exception {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", "sss@ggg.com");
        params.put("space", "aaa bbb");
        params.put("russian", "привет");
        params.put("some_chars", "!@#$%^&*()_+|");


        server.addHandler("/formParams", new SimpleHttpHandlerAdapter() {
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                assertEquals("Form should be url-encoded",
                        "application/x-www-form-urlencoded; charset=UTF-8",
                        httpRequestContext.getRequestHeaders().get(HTTP.CONTENT_TYPE).get(0));
                assertEquals(httpRequestContext.getRequestBody().length,
                        Integer.valueOf(httpRequestContext.getRequestHeaders().get("Content-length").get(0)).intValue());

                try {
                    String postParams = new String(httpRequestContext.getRequestBody());
                    String[] paramValuePairs = postParams.split("\\&");
                    for (String paramValuePair: paramValuePairs) {
                        String[] paramValueArray = paramValuePair.split("\\=");
                        String param = paramValueArray[0];
                        String value = java.net.URLDecoder.decode(paramValueArray[1], "UTF-8");
                        assertEquals("incorrect param value", value, params.get(param));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return "OK".getBytes();
            }
        });

        for (Map.Entry<RequestMethod, Class<? extends WebRequest>> entry: allRequests.entrySet()) {
            if (entry.getKey().equals(RequestMethod.POST) || entry.getKey().equals(RequestMethod.PUT)) {
                EntityEnclosingWebRequest req = (EntityEnclosingWebRequest)entry.getValue().newInstance();
                req.setUrl(server.getBaseUrl() + "/formParams");
                req.addFormParams(params);

                wb.getResponse(req);
            }
        }
    }


    @Test
    public void testGzipResponse() throws Exception {

        final String responseText = "Hello from SimpleHttperver";

        server.addHandler("/gzip", new SimpleHttpHandlerAdapter() {
            public byte[] getResponse(HttpRequestContext httpRequestContext) {
                byte[] out = null;

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    OutputStream os = new GZIPOutputStream(baos);
                    os.write(responseText.getBytes());
                    os.flush();
                    os.close();
                    out = baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                setResponseHeader("Content-Encoding", "gzip");
                setResponseHeader("Content-length", String.valueOf(out.length));
                return out;
            }
        });

        WebRequest req = new HttpGetWebRequest(server.getBaseUrl() + "/gzip");
        WebResponse resp = wb.getResponse(req);
        assertEquals("Response from server is incorrect", responseText, resp.getText());
    }

    @Test
    public void testPostWithBodyRequest() throws Exception {
        final String requestParam1 = "Test request body";
        final String requestParam2 = "Test request body string";
        final byte[] body = requestParam1.getBytes();

        final String requestBody = "--zeNfQFxOvIRY_1tTWU-9ArUdJpMkKi9\n" +
                "Content-Disposition: form-data; name=\"param1\"\n" +
                "Content-Type: application/octet-stream\n" +
                "Content-Transfer-Encoding: binary\n" +
                "\n" +
                "Test request body\n" +
                "--zeNfQFxOvIRY_1tTWU-9ArUdJpMkKi9\n" +
                "Content-Disposition: form-data; name=\"param2\"\n" +
                "Content-Type: text/plain; charset=US-ASCII\n" +
                "Content-Transfer-Encoding: 8bit\n" +
                "\n" +
                "Test request body string\n" +
                "--zeNfQFxOvIRY_1tTWU-9ArUdJpMkKi9--";

        server.addHandler("/postWithBody", new SimpleHttpHandlerAdapter() {
            public byte[] getResponse(HttpRequestContext httpRequestContext) {
                try {
                    byte[] body = httpRequestContext.getRequestBody();
                    assertEquals(
                            requestBody.replaceAll("--.*", "").replaceAll("[\\r\\n]+", ""),
                            new String(body).replaceAll("--.*", "").replaceAll("[\\r\\n]+", ""));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                assertEquals("Method should be POST", "POST", httpRequestContext.getRequestMethod());
                assertTrue(httpRequestContext.getRequestHeaders().get(HTTP.CONTENT_TYPE).get(0)
                        .contains("multipart/form-data"));

                return null;
            }
        });

        EntityEnclosingWebRequest req = new HttpPostWebRequest(server.getBaseUrl() + "/postWithBody");
        req.addPart("param1", body);
        req.addPart("param2", requestParam2);

        wb.getResponse(req);
    }
}
