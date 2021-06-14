package org.example.build;

import org.example.core.ClientSession;
import org.example.entity.ProtoMsg;
import org.example.entity.User;

/**
 * @Classname HeartBeatMsgBuilder
 * @Description TODO
 * @Date 2021/6/11 22:07
 * @Created by wangchao
 */
public class HeartBeatMsgBuilder extends BaseBuilder {
    private final User user;
    public HeartBeatMsgBuilder(User user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message buildMsg() {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageHeartBeat.Builder lb =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUid());
        return message.toBuilder().setHeartBeat(lb).build();
    }

}
