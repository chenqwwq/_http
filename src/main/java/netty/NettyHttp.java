package netty;

import common.entity.HttpConfig;
import common.interfaces.HttpServerBootStrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * The type Netty http.
 *
 * @author chen
 * @date 2020 /3/8 上午10:44
 */
public class NettyHttp implements HttpServerBootStrap {
    /**
     * 默认端口
     */
    private static final int PORT = 8080;

    /**
     * 服务端监听线程数
     */
    private static final int MAX_BOSS_THREAD_NUM = 1;

    /**
     * io线程数
     */
    private static final int MAX_WORKER_THREAD_NUM = 3;

    private static ServerBootstrap serverBootstrap;

    private static EventLoopGroup bossGroup;

    private static EventLoopGroup workerGroup;

    static {
        serverBootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(MAX_BOSS_THREAD_NUM);
        workerGroup = new NioEventLoopGroup(MAX_WORKER_THREAD_NUM);
    }

    public NettyHttp() {
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }



    @Override
    public void start(HttpConfig config) throws Exception {

    }
}
