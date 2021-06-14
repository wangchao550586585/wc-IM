package org.example.entity;

import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
public class ChatMsg {


    //消息类型  1：纯文本  2：音频 3：视频 4：地理位置 5：其他
    public enum MSGTYPE {
        TEXT,
        AUDIO,
        VIDEO,
        POS,
        OTHER;
    }

    public ChatMsg(User user) {
        if (null == user) {
            return;
        }
        this.user = user;
        this.setTime(System.currentTimeMillis());
        this.setFrom(user.getUid());
        this.setFromNick(user.getNickName());

    }

    private User user;

    private long msgId;
    private String from;
    private String to;
    private long time;
    private MSGTYPE msgType;
    private String content;
    private String url;          //多媒体地址
    private String property;     //附加属性
    private String fromNick;     //发送者昵称
    private String json;         //附加的json串


    public void fillMsg(ProtoMsg.MessageRequest.Builder cb) {
        if (msgId > 0) {
            cb.setMsgId(msgId);
        }
        if (!ObjectUtils.isEmpty(from)) {
            cb.setFrom(from);
        }
        if (!ObjectUtils.isEmpty(to)) {
            cb.setTo(to);
        }
        if (time > 0) {
            cb.setTime(time);
        }
        if (msgType != null) {
            cb.setMsgType(msgType.ordinal());
        }
        if (!ObjectUtils.isEmpty(content)) {
            cb.setContent(content);
        }
        if (!ObjectUtils.isEmpty(url)) {
            cb.setUrl(url);
        }
        if (!ObjectUtils.isEmpty(property)) {
            cb.setProperty(property);
        }
        if (!ObjectUtils.isEmpty(fromNick)) {
            cb.setFromNick(fromNick);
        }

        if (!ObjectUtils.isEmpty(json)) {
            cb.setJson(json);
        }
    }

}
