package org.example.sender;

import lombok.extern.slf4j.Slf4j;
import org.example.build.ChatMsgBuilder;
import org.example.entity.ChatMsg;
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
public class ChatSender extends BaseSender {

    public void sendChatMsg(String toUserId, String content) {
        ChatMsg chatMsg = new ChatMsg(getUser());
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MSGTYPE.TEXT);
        chatMsg.setTo(toUserId);
        chatMsg.setMsgId(System.currentTimeMillis());
        ProtoMsg.Message message = ChatMsgBuilder.buildChatMsg(chatMsg, getUser(), getSession());
        super.sendMsg(message);
    }
    @Override
    protected void sendSucced(ProtoMsg.Message message) {
        log.info("发送成功:" + message.getMessageRequest().getContent());
    }



    @Override
    protected void sendfailed(ProtoMsg.Message message) {
        log.info("发送失败:" + message.getMessageRequest().getContent());
    }

}
