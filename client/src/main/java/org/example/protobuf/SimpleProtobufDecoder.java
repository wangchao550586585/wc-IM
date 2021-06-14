package org.example.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.example.entity.ProtoMsg;
import org.example.exception.InvalidFrameException;

import java.util.List;
import java.util.Objects;

/**
 * @Classname SimpleProtobufDecoder
 * @Description TODO
 * @Date 2021/6/11 20:51
 * @Created by wangchao
 */
public class SimpleProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Object outMsg = decode0(channelHandlerContext, byteBuf);
        if (!Objects.isNull(outMsg)) {
            list.add(outMsg);
        }
    }

    private Object decode0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws InvalidFrameException, InvalidProtocolBufferException {
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() < 8) {
            return null;
        }
        short magic = byteBuf.readShort();
        if (magic != ProtoInstant.MAGIC_CODE) {
            String error = "客户端口令不对:" + channelHandlerContext.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }
        short version = byteBuf.readShort();
        if (version != ProtoInstant.VERSION_CODE) {
            channelHandlerContext.close();
        }
        int length = byteBuf.readInt();
        if (length < 0) {
            channelHandlerContext.close();
        }
        if (length > byteBuf.readableBytes()) {
            //读取位置不够，则回退
            byteBuf.resetReaderIndex();
            return null;
        }
        byte[] data = new byte[length];
        if (byteBuf.hasArray()) {
            data = byteBuf.slice(byteBuf.readerIndex(),length).array();
        } else {
            byteBuf.readBytes(data, 0, length);
        }
        ProtoMsg.Message message = ProtoMsg.Message.parseFrom(data);
        return message;
    }
}
