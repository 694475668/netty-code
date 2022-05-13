package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author: bright
 * @date:Created in 2022/5/1 8:31
 * @describe :
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //1.创建二个线程组bossGroup和workGroup
        //2.bossGroup只是处理连接请求，真正的和客户端业务处理会交给workGroup完成
        //3.二个都是无线循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //创建服务器启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置二个线程
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到的连接数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动的连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //handler是给bossGroup |childHandler是给 workGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());

                        }
                    });
            System.out.println("服务器 is read");
            ChannelFuture sync = serverBootstrap.bind(6669).sync();
            //可以注册监听事件，监控我们概念性的事件，这里是测试
            sync.addListener(new ChannelFutureListener() {
                //isDone  判断当前操作是否完成
                //isSuccess 判断已成为的操作是否成功
                //isCancelled 判断已完成的操作是否被取消
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isDone()) {
                        System.out.println("端口绑定成功");
                    } else {
                        System.out.println("监听端口失败");
                    }
                }
            });
            sync.channel().closeFuture().sync();
        } finally {
            //关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
