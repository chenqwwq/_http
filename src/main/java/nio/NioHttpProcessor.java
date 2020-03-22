package nio;

import common.entity.*;
import common.enums.HttpMethod;
import common.enums.HttpStatusCode;
import common.exception.BadRequestException;
import common.exception.StatusCodeException;
import common.interfaces.HttpProcessor;
import common.interfaces.impl.AbstractHttpProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 具体的工作线程，在Poller中轮询检测事件，并交给该事件执行
 *
 * @author chen
 * @date 2020/3/18 下午8:18
 */
@Slf4j
public class NioHttpProcessor extends AbstractHttpProcessor<SocketChannel> implements HttpProcessor<SocketChannel>, Runnable {

    private static final int DEFAULT_BYTE_BUFFER_SIZE = 4096;

    private SelectionKey selectionKey;

    /**
     * Instantiates a new Abstract http processor.
     *
     * @param httpConfig the http config
     * @throws Exception the exception
     */
    protected NioHttpProcessor(HttpConfig httpConfig, SelectionKey selectionKey) throws Exception {
        super(httpConfig);
        this.selectionKey = selectionKey;
    }

    @SneakyThrows
    @Override
    public void run() {

        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        if (this.selectionKey.isReadable()) {
            log.info("// ========= 触发读事件");
            HttpResponse httpResponse;
            try {
                httpResponse = doRequest(getRequest(channel));
            } catch (Exception e) {
                httpResponse = errorPageCache.getHttpResponse(e instanceof StatusCodeException ? ((StatusCodeException) e).getCode() : HttpStatusCode.INTERNAL_SERVER_ERROR);
            }
            selectionKey.attach(httpResponse);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
        if (selectionKey.isWritable()) {
            log.info("// ========= 触发写事件");
            final Object attachment = selectionKey.attachment();
            if (!(attachment instanceof HttpResponse)) {
                return;
            }
            printResponse(channel, (HttpResponse) attachment);
            selectionKey.interestOps(SelectionKey.OP_READ);
        }

    }

    @Override
    public HttpRequest getRequest(SocketChannel socketChannel) throws Exception {
        // 构造缓冲区
        StringBuilder stringBuilder = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE);
        // 循环读取整个请求的内容
        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            stringBuilder.append(new String(buffer.array(), StandardCharsets.UTF_8));
            buffer.flip();
            buffer.clear();
        }

        // 按照\r\n拆分整个请求体
        final String[] allRequest = stringBuilder.toString().split("\r\n");

        // 构造http的请求头对象
        // 第一个字符串对象就是请求头
        final String[] requestStrArr = allRequest[0].split(" ");
        if (requestStrArr.length < 3) {
            throw new BadRequestException("request line error");
        }
        HttpRequestLine httpRequestLine = new HttpRequestLine(HttpMethod.getHttpMethod(requestStrArr[0]), requestStrArr[1], requestStrArr[2]);

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        for (int i = 1; i < allRequest.length; i++) {
            headers.addHeader(allRequest[i]);
        }

        return new HttpRequest(httpRequestLine, headers, null);
    }

    @Override
    public void printResponse(SocketChannel socketChannel, HttpResponse httpResponse) throws IOException {
        socketChannel.write(ByteBuffer.wrap(httpResponse.toString().getBytes(StandardCharsets.UTF_8)));
    }
}
