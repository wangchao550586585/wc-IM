package org.example.core;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.command.*;
import org.example.entity.User;
import org.example.sender.ChatSender;
import org.example.sender.LoginSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @Classname CommandController
 * @Description TODO
 * @Date 2021/6/10 23:00
 * @Created by wangchao
 */
@Service
@Data
@Slf4j
public class CommandController {
    private Map<String, BaseCommand> commandMap;
    private volatile boolean connectFlag = false;
    private ClientSession session;
    private Channel channel;
    private User user;
    @Autowired
    private LoginSender loginSender;
    @Autowired
    private ChatSender chatSender;
    @Autowired
    ChatNettyClient chatNettyClient;

    public void register(Collection<BaseCommand> baseCommands) {
        commandMap = Maps.newHashMap();
        baseCommands.forEach(k -> {
            commandMap.put(k.getKey(), k);
        });
        ((ClinetMenuCommand) commandMap.get(ClinetMenuCommand.KEY)).setAllCommand(commandMap);
    }

    public void commandThreadRunning() throws InterruptedException {
        Thread.currentThread().setName("命令线程");
        while (true) {
            while (!connectFlag) {
                startConnectServer();
                waitCommandThread();
            }
            while (!Objects.isNull(session)) {
                Scanner scanner = new Scanner(System.in);
                ClinetMenuCommand clinetMenuCommand = (ClinetMenuCommand) commandMap.get(ClinetMenuCommand.KEY);
                clinetMenuCommand.exec(scanner);
                String key = clinetMenuCommand.getCommandInput();

                BaseCommand command = commandMap.get(key);
                if (Objects.isNull(session)) {
                    System.err.println("无法识别[" + command + "]指令，请重新输入!");
                    continue;
                }

                switch (key) {
                    case ChatConsoleCommand.KEY:
                        command.exec(scanner);
                        startOneChat((ChatConsoleCommand) command);
                        break;
                    case LoginConsoleCommand.KEY:
                        command.exec(scanner);
                        startLogin((LoginConsoleCommand) command);
                        break;
                    case LogoutConsoleCommand.KEY:
                        command.exec(scanner);
                        startLogout((LogoutConsoleCommand) command);
                        break;
                }
            }
        }
    }

    private synchronized void waitCommandThread() {
        //休眠，命令收集线程
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ChannelFutureListener closeListener = channelFuture -> {
        ClientSession session = channelFuture.channel().attr(ClientSession.SESSION_KEY).get();
        session.close();
        notifyCommandThread();
    };

    ChannelFutureListener channelFutureListener = channelFuture -> {
        EventLoop eventLoop = channelFuture.channel().eventLoop();
        if (!channelFuture.isSuccess()) {
            eventLoop.schedule(() -> chatNettyClient.doConnect(), 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;
            channel = channelFuture.channel();
            session = new ClientSession(channel);
            session.setConnected(true);
            channel.closeFuture().addListener(closeListener);
            notifyCommandThread();
        }
    };

    private synchronized void notifyCommandThread() {
        this.notify();

    }


    public void startConnectServer() {
        FutureTaskScheduler.submit(() -> {
            chatNettyClient.setConnectedListener(channelFutureListener);
            chatNettyClient.doConnect();
        });
    }

    private void startLogout(LogoutConsoleCommand command) {
        //登出
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        //todo 登出
    }

    private void startLogin(LoginConsoleCommand command) {
        if (!isConnectFlag()) {
            log.info("还没有登录，请先登录");
            return;
        }
        User user = new User();
        user.setUid(command.getUsername());
        user.setToken(command.getPassword());
        user.setDevId("1111");
        this.user = user;
        session.setUser(user);
        loginSender.setUser(user);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
    }

    private void startOneChat(ChatConsoleCommand command) {
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        chatSender.setUser(user);
        chatSender.setSession(session);
        chatSender.sendChatMsg(command.getToUserId(),command.getMessage());

    }

    private boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }
}
