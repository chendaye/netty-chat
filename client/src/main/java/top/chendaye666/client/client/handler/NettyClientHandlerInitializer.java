package top.chendaye666.client.client.handler;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageProtobufDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建的 NettyClientHandlerInitializer 类，就继承了 ChannelInitializer 抽象类，
 * 实现和服务端建立连接后，添加相应的 ChannelHandler 处理器。
 */
@Component
public class NettyClientHandlerInitializer extends ChannelInitializer<Channel> {

    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 60;


    @Autowired
    private MessageProtobufDispatcher messageProtobufDispatcher;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                // 空闲检测
                .addLast(new IdleStateHandler(READ_TIMEOUT_SECONDS, 0, 0))
                .addLast(new ReadTimeoutHandler(3 * READ_TIMEOUT_SECONDS))
                // 添加ProtobufVarint32FrameDecoder解码器，主要用于Protobuf的半包处理
                .addLast(new ProtobufVarint32FrameDecoder())
                /*添加ProtobufDecoder解码器，它的参数是com.google.protobuf.MessageLite，实际上就是告诉ProtobufDecoder需要解码的目标类是什么，
                   否则仅仅从字节数组中是无法判断出要解码的目标类型信息的（客户端需要解析的是服务端请求，所以是Response）*/
                .addLast(new ProtobufDecoder(InvocationPojo.Invocation.getDefaultInstance()))
                // 添加Protobuf...FieldPrepender编码器，主要用于Protobuf的半包处理
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                // 添加ProtobufEncoder编码器
                .addLast(new ProtobufEncoder())
                // 消息分发
                .addLast(messageProtobufDispatcher)
                // 客户端处理器
                .addLast(nettyClientHandler)
        ;
    }

}
