package org.example.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.example.core.ClientSession;
import org.example.entity.ProtoMsg;
import org.example.protobuf.ProtoInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname LoginResponseHandler
 * @Description TODO
 * @Date 2021/6/11 20:52
 * @Created by wangchao
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private HeartBeatClientHandler heartBeatClientHandler;
    @Autowired
    private ChatMsgHandler chatMsgHandler;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (!headType.equals(ProtoMsg.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.LoginResponse loginResponse = pkg.getLoginResponse();
        ProtoInstant.ResultCodeEnum result =
                ProtoInstant.ResultCodeEnum.values()[loginResponse.getCode()];

        if (!result.equals(ProtoInstant.ResultCodeEnum.SUCCESS)) {
            log.info(result.getDesc());
        } else {
            ClientSession.loginSuccess(ctx, pkg);
            ChannelPipeline pipeline = ctx.pipeline();
            //删除响应handler,在编码器后面,添加插入心跳处理器
            pipeline.remove(this);
            pipeline.addAfter("encoder", "heartbeat", heartBeatClientHandler);
            pipeline.addAfter("encoder", "chat", chatMsgHandler);
            heartBeatClientHandler.channelActive(ctx); //开始发送心跳
        }


    }
}
