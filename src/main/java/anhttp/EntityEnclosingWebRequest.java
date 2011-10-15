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

import org.apache.http.entity.mime.content.ContentBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Web request for methods which allows to send
 * data inside request body (like {@code POST, PUT})
 *
 * @author Sergey Prilukin
 */
public interface EntityEnclosingWebRequest extends WebRequest {

    /**
     * <p>Add content of {@code file} parameter which will be sent as multipart form data with request.</p>
     * <p>Please see note for {@link #getFormParams}</p>
     *
     * @param partName name of this part
     * @param file file which content will be added
     * @param name name of file which will be reported to server
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @param charset character set of this part. By default {@code UTF-8} will be used used
     * @throws FileNotFoundException if file can not be found
     */
    public void addPart(String partName, File file, String mimeType, String charset, String name) throws IOException;

    /**
     * <p>Same as {@link #addPart(String, java.io.File, String, String, String)}.</p>
     * <p>name will be set to {@code null}</p>
     *
     * @param partName name of this part
     * @param file file which content will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @param charset character set of this part. By default {@code UTF-8} will be used used
     * @throws FileNotFoundException if file can not be found
     */
    public void addPart(String partName, File file, String mimeType, String charset) throws IOException;

    /**
     * <p>Same as {@link #addPart(String, java.io.File, String, String)}.</p>
     * <p>will be used default charset: {@code US-ASCII}</p>
     *
     * @param partName name of this part
     * @param file file which content will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @throws FileNotFoundException if file can not be found
     */
    public void addPart(String partName, File file, String mimeType) throws IOException;

    /**
     * <p>Same as {@link #addPart(String, java.io.File, String)}.</p>
     * <p>will be used default MIME type: {@code application/octet-stream}</p>
     *
     * @param partName name of this part
     * @param file file which content will be added
     * @throws FileNotFoundException if file can not be found
     */
    public void addPart(String partName, File file) throws IOException;

    /**
     * <p>Add content of {@code inputStream} parameter which will be sent as multipart form data with request.</p>
     * <p>Please see note for {@link #getFormParams}</p>
     *
     * @param partName name of this part
     * @param inputStream inputStream which content will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @param name name of this part which will be reported as file name
     * @throws IOException if exception occurred
     */
    public void addPart(String partName, InputStream inputStream, String mimeType, String name) throws IOException;

    /**
     * <p>Same as {@link #addPart(String, java.io.InputStream, String, String)}.</p>
     * <p>name will be set to {@code null}</p>
     *
     * @param partName name of this part
     * @param inputStream inputStream which content will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @throws IOException if exception occurred
     */
    public void addPart(String partName, InputStream inputStream, String mimeType) throws IOException;

    /**
     * <p>Same as {@link #addPart(String, java.io.InputStream, String)}.</p>
     * <p>will be used default MIME type: {@code application/octet-stream}</p>
     *
     * @param partName name of this part
     * @param inputStream inputStream which content will be added
     * @throws IOException if exception occurred
     */
    public void addPart(String partName, InputStream inputStream) throws IOException;

    /**
     * <p>Add {@code string} parameter which will be sent as multipart form data with request.</p>
     * <p>Please see note for {@link #getFormParams}</p>
     *
     * @param partName name of this part
     * @param string string which  will be added
     * @param charset character set of this part. By default {@code US-ASCII} is used
     */
    public void addPart(String partName, String string, String charset);

    /**
     * <p>Same as {@link #addPart(String, String, String)}.</p>
     * <p>will be used default charset: {@code US-ASCII}</p>
     *
     * @param partName name of this part
     * @param string string which  will be added
     */
    public void addPart(String partName, String string);

    /**
     * <p>Add {@code byteArray} parameter which will be sent as multipart form data with request.</p>
     * <p>Please see note for {@link #getFormParams}</p>
     *
     * @param partName name of this part
     * @param byteArray byte array which  will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     * @param name name of this part which will be reported as file name
     */
    public void addPart(String partName, byte[] byteArray, String mimeType, String name);

    /**
     * <p>Same as {@link #addPart(String, byte[], String, String)}.</p>
     * <p>{@code null} will be used as name</p>
     *
     * @param partName name of this part
     * @param byteArray byte array which  will be added
     * @param mimeType MIME type of this part. By default {@code application/octet-stream} will be used
     */
    public void addPart(String partName, byte[] byteArray, String mimeType);

    /**
     * <p>Same as {@link #addPart(String, byte[], String)}.</p>
     * <p>will be used default MIME type: {@code application/octet-stream}</p>
     *
     * @param partName name of this part
     * @param byteArray byte array which  will be added
     */
    public void addPart(String partName, byte[] byteArray);

    /**
     * <p>Returns {@link Map} of parts which will be sent as multipart data during request</p>
     * <p>Please see note for {@link #getFormParams}</p>
     *
     * @return map of parts of multipart request data
     */
    public Map<String, ContentBody> getParts();

    /**
     * <p>Return collection of (name, value) pairs of parameters which
     * will be sent to server in request body as url-encoded data
     * when web browser will execute this web request.</p><br />
     *
     * <p><b>NOTE:</b> request can sent either multipart data
     * see {@link #addPart} or form params.</p>
     *
     * <p>It depends on method from which group was called last time before
     * sending request</p>
     *
     * @return collection of request params which will be sent as url-encoded data in request body
     */
    public Map<String, String> getFormParams();

    /**
     * Add parameter to form params. See {@link #getFormParams}
     *
     * @param requestParams collection of (name, value) pairs of form request params
     */
    public void addFormParams(Map<String, String> requestParams);

    /**
     * Add parameter to form params. See {@link #getFormParams}
     *
     * @param requestParams collection of (name, value) pairs of form request params
     * @param charset charset of form parameters {@code UTF-8} will be used by default
     */
    public void addFormParams(Map<String, String> requestParams, String charset);

    /**
     * Add form request parameter to this request, see {@link #getFormParams}
     *
     * @param name  name of form requets parameter
     * @param value value of form request parameter
     */
    public void addFormParam(String name, String value);

    /**
     * Add form request parameter to this request, see {@link #getFormParams}
     *
     * @param name  name of form requets parameter
     * @param value value of form request parameter
     * @param charset charset of all form params. If this method was called with different charset it will be overridden.
     */
    public void addFormParam(String name, String value, String charset);

    /**
     * Returns charset of form parameters.
     * Willretun {@code UTF-8} if not set.
     *
     * @return charset of form params
     */
    public String getFormParamsCharset();
}
