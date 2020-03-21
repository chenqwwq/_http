package nio;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Nio http boot strap.
 *
 * @author chen
 * @date 2020 /3/17 下午9:56
 */
@Slf4j
public class NioHttpBootStrap implements HttpServerBootStrap {


    private ConcurrentLinkedQueue<SocketChannel> socketChannels;

    /**
     * Instantiates a new Nio http boot strap.
     */
    public NioHttpBootStrap() {
        this.socketChannels = new ConcurrentLinkedQueue<>();
    }

    private static final ExecutorService worker = Executors.newSingleThreadExecutor();

    @Override
    public void start(HttpConfig config) throws Exception {
        // 定义一个管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(config.getPort()));

        // 设置阻塞形式运行
        serverSocketChannel.configureBlocking(true);

        // 启动轮询线程
        worker.execute(new NioHttpPoller(config, socketChannels));

        // 主线程只管将连接加入到并发安全的队列
        for (; ; ) {
            final SocketChannel accept = serverSocketChannel.accept();
            socketChannels.offer(accept);
            log.info("// ======= 新增一个连接");
        }
    }

    private static class NioHttpPollerThreadFactory implements ThreadFactory {

        /**
         * The Atomic integer.
         */
        AtomicInteger atomicInteger;

        /**
         * Instantiates a new Bio http processor thread factory.
         */
        public NioHttpPollerThreadFactory() {
            this.atomicInteger = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "nio-http-poller-" + atomicInteger.getAndIncrement());
        }
    }
}
