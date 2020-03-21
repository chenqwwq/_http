package common.interfaces.impl;

import com.alibaba.fastjson.JSONObject;
import common.ErrorPageCache;
import common.GlobalContent;
import common.entity.*;
import common.enums.HttpStatusCode;
import common.exception.BadRequestException;
import common.exception.ForbiddenException;
import common.exception.NotFoundException;
import common.exception.StatusCodeException;
import common.interfaces.HttpProcessor;
import lombok.SneakyThrows;
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
    protected static ErrorPageCache errorPageCache;

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

    @SneakyThrows
    @Override
    public void process(T i) {
        try {
            printResponse(i, doRequest(getRequest(i)));
        } catch (Exception e) {
            printResponse(i, errorPageCache.getHttpResponse(e instanceof StatusCodeException ? ((StatusCodeException) e).getCode() : HttpStatusCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public HttpResponse doRequest(HttpRequest httpRequest) throws IOException, NotFoundException, ForbiddenException, BadRequestException {
        if (httpRequest == null) {
            throw new BadRequestException("http request not found");
        }
        // 确定访问资源位置
        log.info("// ======= doRequest");

        // 能到这一步的都是http的整个报文正确解析的

        // 获取请求的资源地址
        final String uri = httpRequest.getHttpRequestLine().getUri();

        // 配置http的返回头
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
    private byte[] readFile(String path) throws IOException, NotFoundException, ForbiddenException {
        // 判断是否为文件服务器
        final File file;
        file = path.startsWith(httpConfig.getFilePre()) || path.startsWith("/" + httpConfig.getFilePre())
                ? new File(httpConfig.getDir() + "/" + path.replaceFirst(httpConfig.getFilePre(), ""))
                : new File(ErrorPageCache.FILE_PATH + ("/".equals(path) ? "/page/200.html" : path));

        log.info("// ========== file path : [{}]", file.getPath());
        if (file.isDirectory() || !file.exists()) {
            throw new NotFoundException("error path,file not fount");
        }
        if (!file.canRead()) {
            throw new ForbiddenException("Insufficient permissions");
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
