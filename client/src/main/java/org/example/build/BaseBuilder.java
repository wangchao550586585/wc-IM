package org.example.build;


import org.example.core.ClientSession;
import org.example.entity.ProtoMsg;

/**
 * 基础 Builder
 *
 * @author 尼恩 at  疯狂创客圈
 */
public class BaseBuilder {
    protected ProtoMsg.HeadType type;
    private long seqId;
    private ClientSession session;

    public BaseBuilder(ProtoMsg.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }

    /**
     * 构建消息 基础部分
     */
    public ProtoMsg.Message buildCommon(long seqId) {
        this.seqId = seqId;

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }
    /**
     * 构建消息 基础部分 的 Builder
     */
    public ProtoMsg.Message.Builder baseBuilder(long seqId) {
        this.seqId = seqId;

        ProtoMsg.Message.Builder mb =
                ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb;
    }

}
