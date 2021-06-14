package org.example.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.example.build.HeartBeatMsgBuilder;
import org.example.core.ClientSession;
import org.example.entity.ProtoMsg;
import org.example.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Classname HeartBeatClientHandler
 * @Description TODO
 * @Date 2021/6/11 22:02
 * @Created by wangchao
 */
@Service
@ChannelHandler.Sharable
@Slf4j
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 50;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        User user = session.getUser();
        HeartBeatMsgBuilder builder = new HeartBeatMsgBuilder(user, session);
        ProtoMsg.Message heartbeatMsg = builder.buildMsg();
        heartBeat(ctx, heartbeatMsg);

    }

    private void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                log.info(" 发送 HEART_BEAT  消息 to server");
                ctx.writeAndFlush(heartbeatMsg);
                heartBeat(ctx, heartbeatMsg);
            }
        },HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        //判断类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {

            log.info(" 收到回写的 HEART_BEAT  消息 from server");
            return;
        } else {
            super.channelRead(ctx, msg);

        }
    }
}
