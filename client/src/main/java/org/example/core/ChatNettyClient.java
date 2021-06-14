package org.example.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.ExceptionHandler;
import org.example.handler.LoginResponseHandler;
import org.example.protobuf.SimpleProtobufDecoder;
import org.example.protobuf.SimpleProtobufEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname ChatNettyClient
 * @Description TODO
 * @Date 2021/6/11 13:06
 * @Created by wangchao
 */
@Service
@Slf4j
@Data
public class ChatNettyClient {
    // 服务器ip地址
//    @Value("${chat.server.ip}")
    private String host = "127.0.0.1";
    // 服务器端口
//    @Value("${chat.server.port}")
    private int port = 8081;

    @Autowired
    private LoginResponseHandler loginResponseHandler;
    @Autowired
    private ExceptionHandler exceptionHandler;

    private GenericFutureListener<ChannelFuture> connectedListener;
    private NioEventLoopGroup group;
    private Bootstrap bootstrap;

    public ChatNettyClient() {
        group = new NioEventLoopGroup();
    }


    public void doConnect() {
        try {
            bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            channel.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            channel.pipeline().addLast(loginResponseHandler);
                            channel.pipeline().addLast(exceptionHandler);
                        }
                    });
            log.info("客户端开始连接 ");
            ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener(connectedListener);
        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
            e.printStackTrace();
        }

    }

}
