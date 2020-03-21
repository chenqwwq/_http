package bio;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Bio http boot strap.
 * 默认以线程池形式处理请求，
 * accept得到连接的socket之后交由线程池处理
 *
 * @author chen
 */
@Slf4j
public class BioHttpBootStrap implements HttpServerBootStrap {

    private static BioHttpProcessor bioHttpProcessor;

    private static final ThreadPoolExecutor th = new ThreadPoolExecutor(1,
            10,
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(16),
            new BioHttpProcessorThreadFactory());


    @Override
    public void start(HttpConfig config) throws Exception {
        // 初始化处理器
        bioHttpProcessor = new BioHttpProcessor(config);
        // 创建对应的socket
        ServerSocket socket = new ServerSocket(config.getPort());
        log.info("// ==========  start bio http socket");
        while (true) {
            Socket accept = socket.accept();
            log.info("// ========== accept server");
            th.execute(new HttpTask(accept));
        }
    }


    private static class HttpTask implements Runnable {

        /**
         * The Socket.
         */
        Socket socket;

        /**
         * Instantiates a new Bio http processor runnable.
         *
         * @param socket the socket
         */
        public HttpTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            bioHttpProcessor.process(socket);
        }
    }


    private static class BioHttpProcessorThreadFactory implements ThreadFactory {

        /**
         * The Atomic integer.
         */
        AtomicInteger atomicInteger;

        /**
         * Instantiates a new Bio http processor thread factory.
         */
        public BioHttpProcessorThreadFactory() {
            this.atomicInteger = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "bio_http_processor-" + atomicInteger.getAndIncrement());
        }
    }

}
