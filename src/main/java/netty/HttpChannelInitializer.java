package netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author chen
 * @date 2020/3/17 下午10:11
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        // Netty中有默认实现的http报文解码器
                        .addLast("http-decoder",new HttpServerCodec())
                        .addLast("http-handler",new HttpServerHandler());
        }
}
