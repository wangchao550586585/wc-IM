package org.example.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.LowSpeedConsumerHandler;
import org.example.protobuf.SimpleProtobufDecoder;
import org.example.protobuf.SimpleProtobufEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangChao
 * @create 2021/6/13 23:19
 */
@Slf4j
@Data
@Service
public class LowSpeedConsumerClient {
    // 服务器ip地址
//    @Value("${chat.server.ip}")
    private String host = "127.0.0.1";
    // 服务器端口
//    @Value("${chat.server.port}")
    private int port = 8081;

    @Autowired
    private LowSpeedConsumerHandler lowSpeedConsumerHandler;

    public void doConnect() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new SimpleProtobufDecoder());
                            ch.pipeline().addLast("encoder", new SimpleProtobufEncoder());
                            ch.pipeline().addLast(lowSpeedConsumerHandler);
                        }
                    });
            log.info("客户端开始连接 [multiEcho Server]");
            ChannelFuture f = bootstrap.connect();//异步发起连接
            f.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.info("客户端 连接 [multiEcho Server] 失败");
                    return;
                }
                log.info("客户端 连接 [echo Server] 成功");
            });
            f.get();

            // 阻塞
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
