package top.chendaye666.server.message.heartbeat;

import top.chendaye666.common.dispatcher.Message;

/**
 * 消息 - 心跳响应
 */
public class HeartbeatResponse implements Message {

    /**
     * 类型 - 心跳响应
     */
    public static final String TYPE = "HEARTBEAT_RESPONSE";

    @Override
    public String toString() {
        return "HeartbeatResponse{}";
    }

}
