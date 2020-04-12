package common.interfaces;

/**
 * 服务主类
 *
 * @author chen
 * @date 2020/4/11 上午10:39
 */
public interface HttpServer {
    /**
     * 启动方法
     **/
    default void start() throws Exception {}
}
