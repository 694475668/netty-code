package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @author: bright
 * @date:Created in 2022/5/2 7:48
 * @describe :
 */
public class ByteBuf02 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world你好", CharsetUtil.UTF_8);
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();
            System.out.println(new String(array));
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.getCharSequence(2, 4, CharsetUtil.UTF_8));
        }
    }
}
