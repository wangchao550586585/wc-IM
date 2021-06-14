package org.example.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.example.build.NotificationMsgBuilder;
import org.example.entity.ProtoMsg;
import org.springframework.stereotype.Service;

/**
 * @author WangChao
 * @create 2021/6/13 17:53
 */
@Slf4j
@ChannelHandler.Sharable
@Service
public class SoulTest1ClientHandler extends ChannelInboundHandlerAdapter {

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ProtoMsg.Message message =
                NotificationMsgBuilder.buildNotification("疯狂创客圈 Netty 灵魂实验 1 " + ctx.channel().id());
        //发送一次消息
        sendMessage(ctx, message);
    }

    private void sendMessage(ChannelHandlerContext ctx, ProtoMsg.Message message) {
        if (ctx.channel().isActive()) {
            log.info("  发送消息 to server");
            ctx.writeAndFlush(message);
        }
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //处理消息的内容
        ProtoMsg.Message notificationPkg = (ProtoMsg.Message) msg;

        String json = notificationPkg.getNotification().getJson();

        log.info("{} 服务端的回复： {}", ctx.channel().id(), json);

    }

}
