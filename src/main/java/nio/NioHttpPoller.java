package nio;

import common.entity.HttpConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chen
 * @date 2020/3/18 下午8:30
 */
@Slf4j
public class NioHttpPoller implements Runnable {

    /**
     * Http的配置类
     */
    private HttpConfig httpConfig;

    /**
     * 每个轮询器最大持有的管道数目
     */
    private static final int MAX_CHANNELS_NUM = 10;

    /**
     * 当前管道数目
     */
    private int channelNum = 0;

    /**
     * 轮询器
     */
    private Selector selector;


    private static final ThreadPoolExecutor worker = new ThreadPoolExecutor(3,
            3,
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(16),
            new NioHttpPoller.NioHttpWorkerThreadFactory());

    /**
     * 几个轮询器之间共享的队列
     */
    private ConcurrentLinkedQueue<SocketChannel> socketChannels;

    public NioHttpPoller(HttpConfig httpConfig, ConcurrentLinkedQueue<SocketChannel> queue) throws IOException {
        this.httpConfig = httpConfig;
        this.selector = Selector.open();
        this.socketChannels = queue;
        log.info("// ========= 新建轮询器成功");
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            if (channelNum < MAX_CHANNELS_NUM && !socketChannels.isEmpty()) {
                // 从队列中获取一个线程
                final SocketChannel poll = socketChannels.poll();
                // 已非阻塞的形式运行
                poll.configureBlocking(false);
                // 注册到
                poll.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                channelNum++;
            }

            if (selector.selectNow() <= 0) {
                continue;
            }

            for (SelectionKey key : selector.selectedKeys()) {
                worker.execute(new NioHttpProcessor(httpConfig, key));
            }
        }
    }


    /**
     * 工作线程的命名
     */
    private static class NioHttpWorkerThreadFactory implements ThreadFactory {

        /**
         * The Atomic integer.
         */
        private AtomicInteger atomicInteger;

        /**
         * Instantiates a new Bio http processor thread factory.
         */
        public NioHttpWorkerThreadFactory() {
            this.atomicInteger = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            log.info("// ======== 新建一个工作线程，当前线程名称：[{}]", Thread.currentThread().getName());
            return new Thread(r, Thread.currentThread().getName() + ":nio-http-worker-" + atomicInteger.getAndIncrement());
        }
    }
}
