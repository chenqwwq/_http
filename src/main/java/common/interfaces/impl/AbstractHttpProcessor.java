package common.interfaces.impl;

import com.alibaba.fastjson.JSONObject;
import common.ErrorPageCache;
import common.GlobalContent;
import common.entity.*;
import common.enums.HttpStatusCode;
import common.exception.NotFoundException;
import common.exception.StatusCodeException;
import common.interfaces.HttpProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * The type Abstract http processor.
 *
 * @param <T> the type parameter
 * @author chen
 * @date 2020 /3/9 上午9:45
 */
@Slf4j
public abstract class AbstractHttpProcessor<T> implements HttpProcessor<T> {
    /**
     * The Pathname.
     */
    final String PATHNAME = "src/main/resources/content-type.json";

    /**
     * The Error page cache.
     */
    protected ErrorPageCache errorPageCache;

    /**
     * The Content type json.
     */
    protected JSONObject contentTypeJson;

    /**
     * 配置对象
     */
    private final HttpConfig httpConfig;

    /**
     * Instantiates a new Abstract http processor.
     *
     * @param httpConfig the http config
     * @throws Exception the exception
     */
    protected AbstractHttpProcessor(HttpConfig httpConfig) throws Exception {
        errorPageCache = new ErrorPageCache();
        contentTypeJson = getContentTypeJson();
        this.httpConfig = httpConfig;
    }

    @Override
    public void process(T i) {
        try {
            printResponse(i, doRequest(getRequest(i)));
        } catch (StatusCodeException codeException) {
            printResponse(i, errorPageCache.getHttpResponse(codeException.getCode()));
        } catch (Exception e) {
            printResponse(i, errorPageCache.getHttpResponse(HttpStatusCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public HttpResponse doRequest(HttpRequest httpRequest) throws IOException, NotFoundException {
        // 确定访问资源位置
        log.info("// ======= doRequest");

        final String uri = httpRequest.getHttpRequestLine().getUri();
        final HttpResponseLine httpResponseLine = new HttpResponseLine(GlobalContent.SUPPORT_HTTP_VERSION, HttpStatusCode.OK);

        // 获取请求的内容信息
        String responseBody = new String(readFile(uri));

        // 获取文件的类型
        final String contentType = String.valueOf(contentTypeJson.get(uri.substring(uri.indexOf(".") + 1)));

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", contentType == null ? "text/html" : contentType + "; charset=UTF-8");
        headers.addHeader("Content-Length", String.valueOf(responseBody.length()));

        return new HttpResponse(httpResponseLine, headers, responseBody);
    }

    // **************************************************
    //      private method
    // **************************************************

    /**
     * 读取文件内容
     */
    private byte[] readFile(String path) throws IOException, NotFoundException {
        File file = new File(ErrorPageCache.FILE_PATH + ("/".equals(path) ? "200.html" : path));
        if (file.isDirectory() || !file.exists()) {
            throw new NotFoundException("error path,file not fount");
        }
        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] res = new byte[(int) randomAccessFile.length()];
            randomAccessFile.read(res);
            return res;
        }
    }

    /**
     * 获取content-type相关的json文件
     */
    private JSONObject getContentTypeJson() throws IOException {
        File file = new File(PATHNAME);
        try (final FileInputStream input = new FileInputStream(file)) {
            return JSONObject.parseObject(IOUtils.toString(input, StandardCharsets.UTF_8));
        }
    }
}
