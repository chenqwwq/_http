package nio;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link java.nio.channels.Selector} 的集合包装类
 *
 * @author chen
 * @date 2020/3/21 下午9:54
 */
@Slf4j
public class Selectors {

    private static final Integer DEFAULT_VALUE = 0;

    /**
     * {@link CopyOnWriteArrayList}提供了新增时的同步保证
     */
    private CopyOnWriteArrayList<Selector> selectorList;

    /**
     * 通过{@link ConcurrentHashMap} 提供并发安全的set，避免相同的selector重复添加
     */
    private ConcurrentHashMap<Selector, Integer> concurrentHashSet;

    /**
     * 最后一次访问的下标
     */
    private AtomicInteger lastAccessIndex = new AtomicInteger(0);

    public Selectors() {
        selectorList = new CopyOnWriteArrayList<>();
        concurrentHashSet = new ConcurrentHashMap<>(16);
    }

    /**
     * 添加Selector，同步安全由JDK的底层结构提供
     *
     * @param selector {@link Selector}
     */
    public void addSelector(Selector selector) {
        if (concurrentHashSet.containsKey(selector) && selectorList.contains(selector)) {
            log.info("// ========== Selector重复");
            return;
        }
        // 以下的都是保证selector并不存在的情况下
        try {
            concurrentHashSet.put(selector, DEFAULT_VALUE);
            selectorList.add(selector);
        } catch (Exception e) {
            // 保证两个集合的一致性，只要报错就双删
            concurrentHashSet.remove(selector);
            selectorList.remove(selector);
        }
    }

    /**
     * 因为线程安全依靠了JDK的实现，但是对于lastAccessIndex来说并非是线程安全的
     * 采用乐观锁的形式,循环获取
     * <p>
     * 就是新加入的Selector可能被跳过
     */
    public Selector getNextSelector() {
        for (; ; ) {
            final int i = lastAccessIndex.get();
            final Selector selector = selectorList.get(i % selectorList.size());
            if (lastAccessIndex.compareAndSet(i, i + 1)) {
                return selector;
            }
        }
    }


}
