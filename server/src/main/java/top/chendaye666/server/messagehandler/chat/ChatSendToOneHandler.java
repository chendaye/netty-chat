package top.chendaye666.server.messagehandler.chat;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageHandler;
import top.chendaye666.server.message.chat.ChatRedirectToUserRequest;
import top.chendaye666.server.message.chat.ChatSendResponse;
import top.chendaye666.server.message.chat.ChatSendToOneRequest;
import top.chendaye666.server.server.manager.NettyChannelProtobufManager;

@Component
public class ChatSendToOneHandler implements MessageHandler<ChatSendToOneRequest> {

    @Autowired
    private NettyChannelProtobufManager nettyChannelProtobufManager;

    @Override
    public void execute(Channel channel, ChatSendToOneRequest message) {
        // 这里，假装直接成功
        ChatSendResponse sendResponse = new ChatSendResponse().setMsgId(message.getMsgId()).setCode(0);
        channel.writeAndFlush(InvocationPojo.Invocation
                .newBuilder().setType(ChatSendResponse.TYPE)
                .setMessage(JSON.toJSONString(sendResponse))
                .build());

        // 创建转发的消息，发送给指定用户
        ChatRedirectToUserRequest sendToUserRequest = new ChatRedirectToUserRequest().setMsgId(message.getMsgId())
                .setContent(message.getContent());
        nettyChannelProtobufManager.send(message.getToUser(), InvocationPojo.Invocation.newBuilder()
                .setType(ChatRedirectToUserRequest.TYPE)
                .setMessage(JSON.toJSONString(sendToUserRequest))
                .build());
    }

    @Override
    public String getType() {
        return ChatSendToOneRequest.TYPE;
    }

}
