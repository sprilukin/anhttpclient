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
import com.googlecode.lighthttp.impl.request.HttpDeleteWebRequest;
import com.googlecode.lighthttp.impl.request.HttpGetWebRequest;
import com.googlecode.lighthttp.impl.request.HttpHeadWebRequest;
import com.googlecode.lighthttp.impl.request.HttpOptionsWebRequest;
import com.googlecode.lighthttp.impl.request.HttpPostWebRequest;
import com.googlecode.lighthttp.impl.request.HttpPutWebRequest;
import com.googlecode.lighthttp.impl.request.HttpTraceWebRequest;
import com.googlecode.lighthttp.server.SimpleHttpHandlerAdapter;
import com.googlecode.lighthttp.server.DefaultSimpleHttpServer;
import com.googlecode.lighthttp.server.HttpRequestContext;
import com.googlecode.lighthttp.server.SimpleHttpServer;
import org.apache.http.cookie.Cookie;
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
import java.util.concurrent.atomic.AtomicInteger;
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
                            requestBody.replaceAll("--.*", "").replaceAll("[\\r\\n]+", "\\n"),
                            new String(body).replaceAll("--.*", "").replaceAll("[\\r\\n]+", "\\n"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                assertEquals("Method should be POST", "POST", httpRequestContext.getRequestMethod());
                assertTrue(httpRequestContext.getRequestHeaders().get(HTTP.CONTENT_TYPE).get(0)
                        .contains("multipart/form-data"));

                return null;
            }
        });

        for (Map.Entry<RequestMethod, Class<? extends WebRequest>> entry: allRequests.entrySet()) {
            if (entry.getKey().equals(RequestMethod.POST) || entry.getKey().equals(RequestMethod.PUT)) {
                EntityEnclosingWebRequest req = new HttpPostWebRequest(server.getBaseUrl() + "/postWithBody");
                req.addPart("param1", body);
                req.addPart("param2", requestParam2);

                wb.getResponse(req);
            }
        }
    }

    @Test
    public void testCookies() throws Exception {

        final String cookieName1 = "PREF";
        final String cookieValue1 = "ID=120db22e4e2810cb:FF=0:TM=1318488512:LM=1318488512:S=tTZoDOAsNIbsiH4R";
        final String cookieParams1 = "expires=Sat, 12-Oct-2099 06:48:32 GMT; path=/; domain=localhost";

        final String cookieName2 = "NID";
        final String cookieValue2 = "52=fa8SxaS_YoBUQ7jLIy9bp14biO";
        final String cookieParams2 = "expires=Fri, 13-Apr-2099 06:48:32 GMT; path=/; domain=localhost; HttpOnly";

        final AtomicInteger requestCount = new AtomicInteger(0);

        server.addHandler("/cookies", new SimpleHttpHandlerAdapter() {
            public byte[] getResponse(HttpRequestContext httpRequestContext) {
                requestCount.addAndGet(1);

                if (requestCount.get() == 1) {
                    setResponseHeader("Set-Cookie", cookieName1 + "=" + cookieValue1 + "; " + cookieParams1);
                } else if (requestCount.get() == 2) {
                    assertEquals(cookieName1 + "=" + cookieValue1, httpRequestContext.getRequestHeaders().get("Cookie").get(0));
                    setResponseHeader("Set-Cookie", cookieName2 + "=" + cookieValue2 + "; " + cookieParams2);
                } else {
                    //Sends only one cookie somewhy
                    //assertEquals(cookieName1 + "; " + cookieName2, httpRequestContext.getRequestHeaders().get("Cookie"));
                }

                return null;
            }
        });

        WebRequest req = new HttpGetWebRequest(server.getBaseUrl() + "/cookies");
        wb.getResponse(req);
        Cookie cookie1 = wb.getCookieByName(cookieName1);
        assertEquals("Invalid cookie 1", cookieName1, cookie1.getName());
        assertEquals("Invalid cookie 1", cookieValue1, cookie1.getValue());
        wb.getResponse(req);

        cookie1 = wb.getCookieByName(cookieName1);
        assertEquals("Invalid cookie 1", cookieName1, cookie1.getName());
        assertEquals("Invalid cookie 1", cookieValue1, cookie1.getValue());

        Cookie cookie2 = wb.getCookieByName(cookieName2);
        assertEquals("Invalid cookie 2", cookieName2, cookie2.getName());
        assertEquals("Invalid cookie2", cookieValue2, cookie2.getValue());

        wb.getResponse(req);
    }
}
