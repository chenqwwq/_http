package nio;

import common.entity.HttpConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 请求队列，所有的请求都会被放到请求队列中
 * <p>
 * 最好用阻塞队列{@link java.util.concurrent.BlockingQueue}实现
 * <p>
 * 其余方法可以是简单的包装实现
 *
 * @author chen
 * @date 2020/3/21 下午10:03
 */
public class ConnectionQueue<T> {

    /**
     * 持有Channel的对象
     */
    private BlockingQueue<T> connectionQueue;

    public ConnectionQueue(HttpConfig httpConfig) {
        this.connectionQueue = new ArrayBlockingQueue<>(httpConfig.getMaxWait());
    }

    public boolean isEmpty() {
        return connectionQueue.isEmpty();
    }

    /**
     * 对外提供的一个包装类，最终还是调用的{@link BlockingQueue#poll()}
     */
    public T getConnection() {
        return connectionQueue.poll();
    }

    public void addConnection(T entry) {
        connectionQueue.offer(entry);
    }

}
