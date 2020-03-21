package bio;

import common.entity.*;
import common.interfaces.impl.AbstractHttpProcessor;
import common.GlobalContent;
import common.interfaces.HttpProcessor;
import common.enums.HttpMethod;
import common.exception.BadRequestException;
import common.exception.HttpVersionNotSupportException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Bio形式的Http请求处理类
 *
 * @author chen
 * @date 2020/3/9 上午10:03
 */
@Slf4j
public class BioHttpProcessor extends AbstractHttpProcessor<Socket> implements HttpProcessor<Socket> {

    /**
     * Instantiates a new Abstract http processor.
     *
     * @param httpConfig the http config
     * @throws Exception the exception
     */
    protected BioHttpProcessor(HttpConfig httpConfig) throws Exception {
        super(httpConfig);
    }

    @Override
    public HttpRequest getRequest(Socket socket) throws BadRequestException, IOException {
        log.info("// ========== getRequest");
        // 声明请求的三要素
        HttpRequestLine httpRequestLine;
        HttpHeaders headers;
        try {
            // 因为InputStream的close会导致socket也关闭，所以不适用try语句关闭
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 读取首行起始行
            final String s = bufferedReader.readLine();
            final String[] startHeaders = s.split(" ");
            httpRequestLine = new HttpRequestLine(HttpMethod.getHttpMethod(startHeaders[0]), startHeaders[1], startHeaders[2]);

            // 检查http的版本
            if (!httpRequestLine.getVersion().equalsIgnoreCase(GlobalContent.SUPPORT_HTTP_VERSION)) {
                throw new HttpVersionNotSupportException("http version not support :" + httpRequestLine.getVersion());
            }

            // 取出请求头信息，不做任何处理
            StringBuilder httpHeaders = new StringBuilder();
            String temp;
            while (!"".equals(temp = bufferedReader.readLine())) {
                httpHeaders.append(temp).append("&");
            }
            headers = new HttpHeaders(httpHeaders.toString());

            // 获取请求体
            // TODO: 获取请求提相关代码
            return new HttpRequest(httpRequestLine, headers, null);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void printResponse(Socket socket, HttpResponse httpResponse) {
        log.info("// ========== print response");
        try {
            final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter.println(httpResponse.toString());
            printWriter.flush();
        } catch (Exception e) {
            // 不往外抛异常
            log.info("// ========  print response failed,message:[{}]", e.getMessage());
        }
    }
}
