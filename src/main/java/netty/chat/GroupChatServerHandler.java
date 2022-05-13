package netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author: bright
 * @date:Created in 2022/5/2 8:27
 * @describe :
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    /**
     * 定义ChannelGroup组 管理所有的channel 单例的
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * 通道就绪就会触发这个方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了");
    }

    /**
     * 通道关闭就会触发这个方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了");
    }

    /**
     * 出现异常会触发
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 当连接建立就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //将当前的channel加入channelGroup
        Channel channel = ctx.channel();
        channelGroup.add(channel);
        //遍历并对所管理的channel发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n");
    }

    /**
     * 断开连接就会触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //遍历并对所管理的channel发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开聊天\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel c = channelHandlerContext.channel();
        //排除自己
        channelGroup.forEach((channel) -> {
            if (channel != c) {
                channel.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + s + "\n");
            }
        });
    }
}
