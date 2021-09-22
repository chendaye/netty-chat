package top.chendaye666.server.messagehandler.chat;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageHandler;
import top.chendaye666.server.message.chat.ChatRedirectToUserRequest;
import top.chendaye666.server.message.chat.ChatSendResponse;
import top.chendaye666.server.message.chat.ChatSendToAllRequest;
import top.chendaye666.server.server.manager.NettyChannelProtobufManager;

@Component
public class ChatSendToAllHandler implements MessageHandler<ChatSendToAllRequest> {

    @Autowired
    private NettyChannelProtobufManager nettyChannelProtobufManager;

    @Override
    public void execute(Channel channel, ChatSendToAllRequest message) {
        // 这里，假装直接成功
        ChatSendResponse sendResponse = new ChatSendResponse().setMsgId(message.getMsgId()).setCode(0);
        channel.writeAndFlush(InvocationPojo.Invocation.newBuilder()
                .setType(ChatSendResponse.TYPE)
                .setMessage(JSON.toJSONString(sendResponse))
                .build());

        // 创建转发的消息，并广播发送
        ChatRedirectToUserRequest sendToUserRequest = new ChatRedirectToUserRequest().setMsgId(message.getMsgId())
                .setContent(message.getContent());
        nettyChannelProtobufManager.sendAll(InvocationPojo.Invocation.newBuilder()
                .setType(ChatRedirectToUserRequest.TYPE)
                .setMessage(JSON.toJSONString(sendToUserRequest))
                .build());
    }

    @Override
    public String getType() {
        return ChatSendToAllRequest.TYPE;
    }

}
