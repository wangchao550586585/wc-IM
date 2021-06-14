package org.example.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ProtoMsg;
import org.example.entity.User;

/**
 * @Classname ClientSession
 * @Description TODO
 * @Date 2021/6/10 23:50
 * @Created by wangchao
 */
@Slf4j
@Data
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    private boolean isConnected = false;
    private Channel channel;
    /**
     * 保存登录后的服务端sessionid
     */
    private String sessionId;
    private boolean isLogin = false;
    private User user ;

    public ClientSession(Channel channel) {
        this.channel = channel;
        this.sessionId = String.valueOf(-1);
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    public static void loginSuccess(ChannelHandlerContext ctx, ProtoMsg.Message pkg) {
        ClientSession session = ctx.channel().attr(ClientSession.SESSION_KEY).get();
        session.setSessionId(pkg.getSessionId());
        session.setLogin(true);
        log.info("登录成功");
    }

    public static ClientSession getSession(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ClientSession.SESSION_KEY).get();
    }


    public void close() {
        isConnected = false;
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.error("连接顺利断开");
                }
            }
        });
    }


}
