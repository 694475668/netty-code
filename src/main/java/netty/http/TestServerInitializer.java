package netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author: bright
 * @date:Created in 2022/5/1 17:44
 * @describe :
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //netty自带的处理http的编码解析器
        socketChannel.pipeline().addLast("MyHttpServerCodec", new HttpServerCodec());
        socketChannel.pipeline().addLast("MyTestHttpServerHandler", new TestHttpServerHandler());
    }
}
