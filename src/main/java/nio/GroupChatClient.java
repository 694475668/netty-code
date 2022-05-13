package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author: Bright
 * @date:Created in 2022/4/29 0029 15:20
 * @describe :
 */
public class GroupChatClient {
    /**
     * 服务器ip
     */
    private static final String HOST = "127.0.0.1";
    /**
     * 服务器端口
     */
    private static final int PORT = 6667;

    private Selector selector;

    private SocketChannel socketChannel;

    private String username;

    /**
     * 初始化工作
     */
    public GroupChatClient() throws Exception {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        //设置非堵塞
        socketChannel.configureBlocking(false);
        //注册
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "\t is ok.....");
    }

    /**
     * 向服务器发送消息
     *
     * @param msg
     */
    public void sendInfo(String msg) {
        msg = username + "说" + msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取服务器端回复的消息
     */
    public void readInfo() {
        try {
            int readChannel = selector.select();
            //有可用的通道
            if (readChannel > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        //得到对应的通道
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //读取
                        channel.read(byteBuffer);
                        //输出数据
                        System.out.println(new String(byteBuffer.array()));
                    }
                    iterator.remove();
                }
            } else {
//                System.out.println("没有可用的通道");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        GroupChatClient groupChatClient = new GroupChatClient();
        //向服务器读取数据
        new Thread(() -> {
            while (true) {
                groupChatClient.readInfo();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String text = scanner.next();
            groupChatClient.sendInfo(text);
        }
    }
}
