package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URL;

/**
 * @author: bright
 * @date:Created in 2022/5/1 17:45
 * @describe : SimpleChannelInboundHandler是ChannelInboundHandlerAdapter子类
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 读取客户端数据
     *
     * @param channelHandlerContext
     * @param httpObject            客户端和服务器相互通信的数据封装成httpObject
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest) {
            System.out.println("msg 类型=" + httpObject.getClass());
            System.out.println("客户端地址" + channelHandlerContext.channel().remoteAddress());

            //过滤favicon.ico
            HttpRequest httpRequest = (HttpRequest) httpObject;
            URL url = new URL(httpRequest.uri());
            if ("/favicon.ico".equals(url.getPath())) {
                return;
            }


            //回复信息给浏览器
            ByteBuf content = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);
            //构造一个http 既httpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            //将构造的response 返回
            channelHandlerContext.writeAndFlush(response);
        }
    }
}
