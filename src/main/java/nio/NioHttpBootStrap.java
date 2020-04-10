package nio;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NIO的启动类
 *
 * @author chen
 * @date 2020 /3/17 下午9:56
 */
@Slf4j
public class NioHttpBootStrap implements HttpServerBootStrap {

    private static final HttpServer INSTANCE = new HttpServer();

    @Override
    public void start() throws Exception {
        INSTANCE.start();
    }

    /**
     * 单例实现启动实例
     */
    static final class HttpServer implements HttpServerBootStrap{
        /**
         * 连接队列
         * {@link #start(HttpConfig) 中会阻塞直到有连接请求，会存入该队列}
         * {@link NioHttpPoller 该类线程负责从连接队列中获取连接并处理}
         */
        private ConnectionQueue<SocketChannel> connectionQueue;

        /**
         * 选择器集合
         */
        private Selectors selectors;

        /**
         * Instantiates a new Nio http boot strap.
         */
        private HttpServer() {
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
        public void start(HttpConfig config) {
            this.connectionQueue = new ConnectionQueue<>(config);
            // 定义一个管道
            try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                serverSocketChannel.bind(new InetSocketAddress(config.getPort()));

                // 设置阻塞形式运行
                // 调用accept时会被阻塞
                // 主线程会阻塞的接受请求
                serverSocketChannel.configureBlocking(true);
                final Selector open = Selector.open();
                // 启动轮询线程
                poller.execute(new NioHttpPoller(config, open, connectionQueue));
                selectors.addSelector(open);

                // 主线程只管将连接加入到队列
                while (!Thread.currentThread().isInterrupted()) {
                    // 阻塞模式运行，等待链接
                    final SocketChannel accept = serverSocketChannel.accept();
                    connectionQueue.addConnection(accept);
                    // selector.select()方法是阻塞的
                    // 因为有新任务进来的就直接获取下个selector并让select()方法直接返回
                    selectors.getNextSelector().wakeup();
                    log.info("// ======= 新增一个连接");
                }
            } catch (IOException e) {
                log.info("// ========== 启动失败");
                System.exit(-1);
            }
        }
    }

}
