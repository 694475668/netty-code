package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: Bright
 * @date:Created in 2022/4/29 0029 10:10
 * @describe :
 */
public class GroupChatServer {
    /**
     * 定义属性
     */
    private Selector selector;


    private ServerSocketChannel serverSocketChannel;

    private static final int PORT = 6667;

    /**
     * 初始化
     *
     * @throws Exception
     */
    public GroupChatServer() throws Exception {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        //设置为非堵塞
        serverSocketChannel.configureBlocking(false);
        //注册
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 监听
     */
    public void listen() throws Exception {
        while (true) {
            //事件处理
            if (selector.select(2000) > 0) {
                //遍历得到SelectionKey集合
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    //取出SelectionKey
                    SelectionKey key = iterator.next();
                    //监听accept
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        //设置为非堵塞
                        socketChannel.configureBlocking(false);
                        //注册
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println(socketChannel.getRemoteAddress() + "上线");
                    }
                    //通道发送read
                    if (key.isReadable()) {
                        readData(key);
                    }
                    //删除iterator
                    iterator.remove();
                }
            } else {
//                System.out.println("等待........");
            }
        }
    }

    /**
     * 读取客户端数据
     *
     * @param key
     * @throws Exception
     */
    public void readData(SelectionKey key) {
        //转型
        SocketChannel socketChannel = null;
        try {
            //转型
            socketChannel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //读入buffer
            int count = socketChannel.read(byteBuffer);
            if (count > 0) {
                //输出数据
                String msg = new String(byteBuffer.array());
                System.out.println(msg);
                //想其它的客户端转发消息
                sendInfoToOther(msg, socketChannel);
            }
        } catch (IOException e) {
            try {
                //退出离线输出
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                //取消注册
                key.channel();
                //关闭通道
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 群发
     *
     * @param msg           内容
     * @param socketChannel 对应的socketChannel
     * @throws Exception
     */
    private void sendInfoToOther(String msg, SocketChannel socketChannel) {
        try {
            System.out.println("服务器转发消息中");
            //获取所有的事件
            Set<SelectionKey> keys = selector.keys();
            //遍历 所有注册到selector上的socketChannel
            for (SelectionKey key : keys) {
                //通过key 取出对应的socketChannel
                Channel targetChannel = key.channel();
                //排除自己
                if (targetChannel instanceof SocketChannel && targetChannel != socketChannel) {
                    //转型
                    SocketChannel dest = (SocketChannel) targetChannel;
                    //将msg存储到buffer
                    ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
                    //写出数据
                    dest.write(byteBuffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
