package org.example.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.SoulTest1ClientHandler;
import org.example.protobuf.SimpleProtobufDecoder;
import org.example.protobuf.SimpleProtobufEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.LockSupport;

/**
 * @author WangChao
 * @create 2021/6/13 17:43
 */
@Service
@Slf4j
@Data
public class SoulTest1Client {
    @Autowired
    private SoulTest1ClientHandler soulTest1ClientHandler;


    //请求的时间间隔，单位为ms
    public static long serverPortCount = 10;

    //连接上限
    public static long maxConnection = 100;

    // 服务器ip地址
    public static String serverHost = "127.0.0.1";
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public SoulTest1Client() {
        group = new NioEventLoopGroup();
    }

    public void doConnect() {
        try {
            bootstrap = new Bootstrap().group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_REUSEADDR, true).handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            ch.pipeline().addLast(soulTest1ClientHandler);
                        }
                    });
            log.info("客户端开始连接 [soul test 1 Server]");
            //客户端无限制发起连接，直到上限
            int indexOfServerPort = 0;
            int serverPort;
            int connectionCount = 0;
            while (!Thread.interrupted()) {
                serverPort = 20000 + indexOfServerPort;
                try {
                    ChannelFuture channelFuture = bootstrap.connect(serverHost, serverPort);
                    channelFuture.addListener(future -> {
                        if (!future.isSuccess()) {
                            log.debug("connect failed, exit!");
                            System.exit(0);
                        } else {
                            log.debug("connect success!!");
                        }
                    });
                    channelFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                indexOfServerPort++;
                if (indexOfServerPort == serverPortCount) indexOfServerPort = 0;
                if (++connectionCount == maxConnection) break;
            }
            LockSupport.parkNanos(Integer.MAX_VALUE * 1000L * 1000L);
        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
        }

    }
}
