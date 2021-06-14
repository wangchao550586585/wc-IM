package org.example.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.build.NotificationMsgBuilder;
import org.example.entity.ProtoMsg;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author WangChao
 * @create 2021/6/13 23:22
 */
@Slf4j
@ChannelHandler.Sharable
@Service
public class LowSpeedConsumerHandler extends ChannelInboundHandlerAdapter {
    //待复制的字符串种子
    String seed = "Java ";
    //待发送的字符串种子
    String toSendContent = null;
    Channel channel = null;
    //线程池：用于定时任务、顺序排队执行任务
    static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
            1,
            new DefaultThreadFactory("seq"));
    //模拟业务执行时间，单位为ms
    public static int mock_business_time = 1000;

    public LowSpeedConsumerHandler() {
        //一次发送4000个字符
        StringBuilder sb = new StringBuilder(4096);
        while (sb.length() < 4000) {
            sb.append(seed);
        }
        this.toSendContent = sb.toString();
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        EXECUTOR.scheduleAtFixedRate(() -> repeatSend(), 500, 500, TimeUnit.MILLISECONDS);
    }

    private void repeatSend() {
        ProtoMsg.Message message =
                NotificationMsgBuilder.buildNotification(toSendContent);
        //发送心跳  消息

        if (channel.isActive()) {
            log.info(" 发送消息 to server");
            channel.writeAndFlush(message);
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

        log.info("{} 收到回复的消息： {}", ctx.channel().id(), json);

        //等待一下，模拟业务处理
        LockSupport.parkNanos(mock_business_time * 1000L * 1000L);
    }
}
