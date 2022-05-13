package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author: bright
 * @date:Created in 2022/5/1 17:45
 * @describe :
 */
public class TestServer {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bootGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TestServerInitializer());
            ChannelFuture sync = serverBootstrap.bind(6664).sync();
            sync.channel().closeFuture().sync();

        } finally {
            bootGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
