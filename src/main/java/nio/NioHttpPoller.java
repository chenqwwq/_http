package nio;

import common.entity.HttpConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
     * 当前管道数目
     */
    private int channelNum = 0;

    /**
     * 几个轮询器之间共享的队列
     */
    private ConnectionQueue<SocketChannel> connectionQueue;

    /**
     * 轮询器
     */
    private Selector selector;

    private static final ExecutorService worker = Executors.newFixedThreadPool(3, new ThreadFactory() {

        private final AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            log.info("// ======== 新增一个IO工作线程");
            return new Thread(r, "nio-http-processor-" + atomicInteger.getAndIncrement());
        }
    });

    public NioHttpPoller(HttpConfig httpConfig, Selector selector, ConnectionQueue<SocketChannel> queue) throws IOException {
        this.httpConfig = httpConfig;
        this.selector = selector;
        this.connectionQueue = queue;
        log.info("// ========= 新建轮询器成功");
    }

    @SneakyThrows
    @Override
    public void run() {
        // 轮询线程就是无限循环的查询以及处理Key中的事件
        while (!Thread.currentThread().isInterrupted()) {
            // 每个线程处理完一轮
            if (channelNum < httpConfig.getMaxChannel() && !connectionQueue.isEmpty()) {
                // 从队列中获取一个线程
                final SocketChannel poll = connectionQueue.getConnection();
                // 已非阻塞的形式运行
                poll.configureBlocking(false);
                // 注册到
                poll.register(selector, SelectionKey.OP_READ);
                channelNum++;
            }

            // select会阻塞到至少有一个通道准备就绪
            // selectNow不会阻塞，会立即返回
            final int select = selector.select(5000);
            if (select <= 0) {
                continue;
            }

            // key中的事件会交给worker处理
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            // 因为提交给线程池处理相当于异步，所以关注的事件仍会持续发生
            while (iterator.hasNext()) {
                final SelectionKey next = iterator.next();
                worker.execute(new NioHttpProcessor(httpConfig, next));
                // 在当前 Poller 上移除已就绪的事件
//                int interestOps = next.interestOps() & (~next.readyOps());
                next.interestOps(next.interestOps() & (~next.readyOps()));
                log.info("添加一次任务");
                iterator.remove();
            }
        }
    }
}
