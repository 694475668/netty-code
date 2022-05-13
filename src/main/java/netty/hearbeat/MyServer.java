package netty.hearbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author: bright
 * @date:Created in 2022/5/2 10:32
 * @describe : 心跳检测
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
                            //加入一个netty 提供的IdleStateHandler
                            // readerIdleTime 表示多长时间没有读，就会发送一个心跳检测包检查是否连接
                            // writerIdleTime 表示多长时间没有写，就会发送一个心跳检测包检查是否连接
                            // allIdleTime 表示多长时间没有读写，就会发送一个心跳检测包检查是否连接
                            //当IdleStateHandler触发后，就会传递给管道的下一个handler去处理，通过
                            //调用下一个handler的userEventTriggered
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            // 加入一个对空闲检查进一步处理的handler（自定义）
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture sync = serverBootstrap.bind(8888).sync();
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
