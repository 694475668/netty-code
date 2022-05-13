package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author: bright
 * @date:Created in 2022/5/2 10:32
 * @describe :
 * HttpServerCodec将请求和应答消息编码或解码为HTTP消息
 * 通常接收到的http是一个片段，如果想要完整接受一次请求所有数据，我们需要绑定HttpObjectAggregator
 * 然后就可以收到一个FullHttpRequest完整的请求信息了
 * ChunkedWriteHandler 向客户端发送HTML5文件，主要用于支持浏览器和服务器进行WebSocket通信
 * WebSocketServerHandler自定义Handler
 */
public class MyServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //在bossGroup 增加一个日志处理器 设置级别INFO
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //因为是基于http所以使用http解码器
                            pipeline.addLast(new HttpServerCodec());
                            //以块方式写，添加ChunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            //http数据在传输过程中是分段，HttpObjectAggregator，就是可以讲多个段聚合,这就是浏览器发送大量数据时，就会发送多次http请求
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //它的数据是以帧形式传输 浏览器请求是ws://localhost:7000/xxx请求
                            //WebSocketServerProtocolHandler 核心功能是将http协议升级为ws协议，保持长连接
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            //自定义handler 处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketServerHandler());
                        }
                    });

            ChannelFuture sync = serverBootstrap.bind(7000).sync();
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
