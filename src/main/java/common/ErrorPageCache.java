package common;

import common.entity.HttpHeaders;
import common.entity.HttpResponse;
import common.entity.HttpResponseLine;
import common.enums.HttpStatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Page constant.
 * <p>
 * 常见错误页面的缓存
 * 使用HashMap保存,
 * key => {@link HttpStatusCode}
 * value => {@link HttpResponse}
 *
 * @author chen
 * @date 2020 /3/9 下午2:33
 */
@Slf4j
public class ErrorPageCache {

    /**
     * 常规网页的存放地址
     */
    public static final String FILE_PATH = "src/main/resources/";

    /**
     * 获取固定的响体
     *
     * @param httpStatusCode the http status code
     * @return http response
     */
    public HttpResponse getHttpResponse(HttpStatusCode httpStatusCode) {
        return ErrorPageCacheInnerObject.HTML_MAP.get(httpStatusCode);
    }

    public HttpResponse getHttpResponse(int code) {
        return ErrorPageCacheInnerObject.HTML_MAP.get(HttpStatusCode.getHttpStatusCode(code));
    }

    /**
     * 以私有静态内部类实现单例模式
     */
    private static class ErrorPageCacheInnerObject extends ErrorPageCache {

        /**
         * 外层只需要通过get来访问该实例，
         * 所以直接设置为私有
         */
        private static ErrorPageCacheInnerObject INSTANCE;

        static {
            try {
                INSTANCE = new ErrorPageCacheInnerObject();
            } catch (Exception e) {
                log.info("errorPageCache init failure , msg : [{}]", e.getMessage());
                System.exit(1);
            }
        }

        final HttpHeaders defaultHeader = new HttpHeaders("Content-Type: text/html; charset=UTF-8&");

        /**
         * 存放一些固定的页面，比如200.html,400.html等
         */
        static Map<HttpStatusCode, HttpResponse> HTML_MAP;


        /**
         * Instantiates a new Page constant.
         *
         * @throws Exception the exception
         */
        public ErrorPageCacheInnerObject() throws Exception {
            log.info("// ======== starting create the error page cache");

            File file = new File(FILE_PATH + "/page");
            final File[] files;
            // 排除
            if (!file.exists() || (files = file.listFiles()) == null || files.length == 0) {
                throw new FileNotFoundException("page not found");
            }

            // 初始化Map，初始长度为/page/下的文件数量
            HTML_MAP = new HashMap<>(files.length);

            // 将HttpStatusCode和HttpResponse绑定
            // 例如400 500等都是固定的页面
            for (File f : files) {
                if (!f.getName().endsWith("html")) {
                    continue;
                }
                // 从html文件中获取状态码
                final String statusCode = f.getName().split("\\.")[0];
                // 获取对应的状态码美剧
                final HttpStatusCode httpStatusCode = HttpStatusCode.getHttpStatusCode(Integer.parseInt(statusCode));
                // 读取文件内容
                final String fileContent = this.readHtmlFile(f);

                // 创建响应体的起始行
                final HttpResponseLine httpResponseLine = new HttpResponseLine("HTTP/1.1", httpStatusCode);

                // 添加响应体长度的请求头
                defaultHeader.addHeader("Content-Length", String.valueOf(fileContent.length()));

                // 构造响应体
                final HttpResponse httpResponse = new HttpResponse(httpResponseLine, defaultHeader, fileContent);

                HTML_MAP.put(httpStatusCode, httpResponse);
            }

            log.info("// ======== create the error page cache success");
        }

        // **********************************************************************
        // **********************    private method    **************************
        // **********************************************************************

        /**
         * 根据状态码获取对应的页面
         *
         * @param file 对应的html
         * @return 页面全部内容
         */
        private String readHtmlFile(File file) throws IOException {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                byte[] fileByte = new byte[(int) randomAccessFile.length()];
                randomAccessFile.read(fileByte);
                return new String(fileByte, StandardCharsets.UTF_8);
            }
        }
    }
}
