package top.chendaye666.server.messagehandler.heartbeat;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageHandler;
import top.chendaye666.server.message.heartbeat.HeartbeatRequest;
import top.chendaye666.server.message.heartbeat.HeartbeatResponse;

@Component
public class HeartbeatRequestHandler implements MessageHandler<HeartbeatRequest> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Channel channel, HeartbeatRequest message) {
        logger.info("[execute][收到连接({}) 的心跳请求]", channel.id());
        // 响应心跳
        HeartbeatResponse response = new HeartbeatResponse();
        channel.writeAndFlush(InvocationPojo.Invocation.newBuilder()
        .setType(HeartbeatResponse.TYPE)
        .setMessage(JSON.toJSONString(response))
        .build());
    }

    @Override
    public String getType() {
        return HeartbeatRequest.TYPE;
    }

}
