package nio;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Nio http boot strap.
 *
 * @author chen
 * @date 2020 /3/17 下午9:56
 */
@Slf4j
public class NioHttpBootStrap implements HttpServerBootStrap {

    private ConnectionQueue<SocketChannel> connectionQueue;

    private Selectors selectors;

    /**
     * Instantiates a new Nio http boot strap.
     */
    public NioHttpBootStrap() {
        selectors = new Selectors();
    }

    /**
     * 轮询线程池
     */
    private static final ExecutorService poller = Executors.newSingleThreadExecutor(new ThreadFactory() {

        private AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "nio-http-poll-" + atomicInteger.getAndIncrement());
        }
    });

    @Override
    public void start(HttpConfig config) throws Exception {
        this.connectionQueue = new ConnectionQueue<>(config);
        // 定义一个管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(config.getPort()));

        // 设置阻塞形式运行
        // 调用accept时会被阻塞
        serverSocketChannel.configureBlocking(true);

        final Selector open = Selector.open();

        // 启动轮询线程
        poller.execute(new NioHttpPoller(config, open, connectionQueue));

        // 主线程只管将连接加入到并发安全的队列
        while (!Thread.currentThread().isInterrupted()) {
            final SocketChannel accept = serverSocketChannel.accept();
            connectionQueue.addConnection(accept);
            // selector.select()方法是阻塞的
            // 因为有新任务进来的就直接获取下个selector并让select()方法直接返回
            selectors.getNextSelector().wakeup();
            log.info("// ======= 新增一个连接");
        }
    }
}
