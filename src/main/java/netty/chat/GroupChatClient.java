package netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author: bright
 * @date:Created in 2022/5/2 8:47
 * @describe :
 */
public class GroupChatClient {

    private final String HOST;
    private final int PORT;

    public GroupChatClient(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    public void run() throws Exception {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //向pipeline加入解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //向pipeline加入编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //自定义pipeline
                            pipeline.addLast(new GroupChatClientHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.connect(HOST, PORT).sync();
            System.out.println("请输入内容");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String text = scanner.nextLine();
                sync.channel().writeAndFlush(text);
            }
            sync.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new GroupChatClient("127.0.0.1", 8888).run();
    }
}
