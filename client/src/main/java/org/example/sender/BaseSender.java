package org.example.sender;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.core.ClientSession;
import org.example.entity.ProtoMsg;
import org.example.entity.User;

/**
 * @Classname BaseSender
 * @Description TODO
 * @Date 2021/6/11 22:48
 * @Data
 * @Slf4j@Created by wangchao
 */
@Data
@Slf4j
public abstract class BaseSender {
    private User user;
    private ClientSession session;

    public boolean isConnected() {
        if (null == session) {
            log.info("session is null");
            return false;
        }
        return session.isConnected();
    }


    protected void sendMsg(ProtoMsg.Message message) {
        if (!isConnected()) {
            log.info("连接还没成功");
            return;
        }

        Channel channel = getSession().getChannel();
        ChannelFuture channelFuture = channel.writeAndFlush(message);
        channelFuture.addListener(future -> {
            if (channelFuture.isSuccess()) {
                sendSucced(message);
            } else {
                sendfailed(message);
            }
        });

    }

    protected void sendSucced(ProtoMsg.Message message) {
        log.info("发送成功");

    }

    protected void sendfailed(ProtoMsg.Message message) {
        log.info("发送失败");
    }


}
