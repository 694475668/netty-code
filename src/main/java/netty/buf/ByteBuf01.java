package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author: bright
 * @date:Created in 2022/5/2 7:48
 * @describe :
 */
public class ByteBuf01 {
    public static void main(String[] args) {
        //在netty中不需要使用flip进行切换
        ByteBuf buffer = Unpooled.buffer(10);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }
        System.out.println(buffer.capacity());

        // 底层会维护一个readIndex和一个writeIndex
        for (int i = 0; i < buffer.capacity(); i++) {
            //buffer.getByte(i)不会导致readIndex进行相加
            System.out.println(buffer.getByte(i));
            //buffer.readByte() 会导致readIndex进行相加
            System.out.println(buffer.readByte());
        }
    }
}
