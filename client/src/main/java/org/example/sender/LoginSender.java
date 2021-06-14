package org.example.sender;

import lombok.extern.slf4j.Slf4j;
import org.example.build.LoginMsgBuilder;
import org.example.entity.ProtoMsg;
import org.springframework.stereotype.Service;

/**
 * @Classname ChatSender
 * @Description TODO
 * @Date 2021/6/11 22:47
 * @Created by wangchao
 */
@Service
@Slf4j
public class LoginSender  extends  BaseSender{

    public void sendLoginMsg() {
        if (!isConnected()) { log.info("还没有建立连接!");return; }
        log.info("构造登录消息");

        ProtoMsg.Message message =
                LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        log.info("发送登录消息");
        super.sendMsg(message);
    }
}
