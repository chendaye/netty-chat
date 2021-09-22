package top.chendaye666.server.messagehandler.auth;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageHandler;
import top.chendaye666.server.message.auth.AuthRequest;
import top.chendaye666.server.message.auth.AuthResponse;
import top.chendaye666.server.server.manager.NettyChannelProtobufManager;

@Component
public class AuthRequestHandler implements MessageHandler<AuthRequest> {

    @Autowired
    private NettyChannelProtobufManager nettyChannelProtobufManager;

    @Override
    public void execute(Channel channel, AuthRequest authRequest) {
        // 如果未传递 accessToken
        if (StringUtils.isEmpty(authRequest.getAccessToken())) {
            AuthResponse authResponse = new AuthResponse().setCode(1).setMessage("认证 accessToken 未传入");
            channel.writeAndFlush(InvocationPojo.Invocation.newBuilder()
                    .setType(AuthResponse.TYPE)
                    .setMessage(JSON.toJSONString(authResponse))
                    .build());
            return;
        }

        //todo： ... 此处应有一段（验证成功）

        // 将用户和 Channel 绑定
        // 考虑到代码简化，我们先直接使用 accessToken 作为 User
        nettyChannelProtobufManager.addUser(channel, authRequest.getAccessToken());

        // 响应认证成功
        AuthResponse authResponse = new AuthResponse().setCode(0);
        channel.writeAndFlush(InvocationPojo.Invocation.newBuilder()
                .setType(AuthResponse.TYPE)
                .setMessage(JSON.toJSONString(authResponse))
                .build());
    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }

}
