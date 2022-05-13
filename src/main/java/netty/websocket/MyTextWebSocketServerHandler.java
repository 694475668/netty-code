package netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * @author: bright
 * @date:Created in 2022/5/2 14:28
 * @describe : TextWebSocketFrame类型表示一个文本帧
 */
public class MyTextWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println("服务器收到消息" + textWebSocketFrame.text());
        //回复消息
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now()) + " : " + textWebSocketFrame.text());
    }

    /**
     * 连接时触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded被调用" + ctx.channel().id().asShortText());
        ctx.channel().write(new TextWebSocketFrame("服务器时间" + LocalDateTime.now()) + " : " + "hahahdd");
    }

    /**
     * 异常触发
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发送" + cause.getMessage());
        ctx.close();
    }

    /**
     * 连接断开触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded被调用" + ctx.channel().id().asLongText());
    }
}
