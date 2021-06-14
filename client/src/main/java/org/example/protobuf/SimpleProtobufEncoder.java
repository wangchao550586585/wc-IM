package org.example.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.entity.ProtoMsg;

/**
 * @Classname SimpleProtobufEncoder
 * @Description TODO
 * @Date 2021/6/11 20:51
 * @Created by wangchao
 */
public class SimpleProtobufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg.Message message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(ProtoInstant.MAGIC_CODE);
        byteBuf.writeShort(ProtoInstant.VERSION_CODE);
        byte[] data = message.toByteArray();
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

    }
}
