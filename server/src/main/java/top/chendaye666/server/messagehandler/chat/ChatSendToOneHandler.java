package top.chendaye666.server.messagehandler.chat;

import top.chendaye666.common.codec.Invocation;
import top.chendaye666.common.dispatcher.MessageHandler;
import top.chendaye666.server.message.chat.ChatSendResponse;
import top.chendaye666.server.message.chat.ChatSendToOneRequest;
import top.chendaye666.server.message.chat.ChatRedirectToUserRequest;
import top.chendaye666.server.server.NettyChannelManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatSendToOneHandler implements MessageHandler<ChatSendToOneRequest> {

    @Autowired
    private NettyChannelManager nettyChannelManager;

    @Override
    public void execute(Channel channel, ChatSendToOneRequest message) {
        // 这里，假装直接成功
        ChatSendResponse sendResponse = new ChatSendResponse().setMsgId(message.getMsgId()).setCode(0);
        channel.writeAndFlush(new Invocation(ChatSendResponse.TYPE, sendResponse));

        // 创建转发的消息，发送给指定用户
        ChatRedirectToUserRequest sendToUserRequest = new ChatRedirectToUserRequest().setMsgId(message.getMsgId())
                .setContent(message.getContent());
        nettyChannelManager.send(message.getToUser(), new Invocation(ChatRedirectToUserRequest.TYPE, sendToUserRequest));
    }

    @Override
    public String getType() {
        return ChatSendToOneRequest.TYPE;
    }

}
