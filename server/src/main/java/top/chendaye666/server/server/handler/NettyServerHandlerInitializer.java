package top.chendaye666.server.server.handler;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import top.chendaye666.common.codec.InvocationPojo;
import top.chendaye666.common.dispatcher.MessageProtobufDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {

    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 3 * 60;

    @Autowired
    private MessageProtobufDispatcher messageProtobufDispatcher;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    protected void initChannel(Channel ch) {
        // 获得 Channel 对应的 ChannelPipeline
        ChannelPipeline channelPipeline = ch.pipeline();
        // 添加一堆 NettyServerHandler 到 ChannelPipeline 中
        channelPipeline
                // 空闲检测
                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                // 添加ProtobufVarint32FrameDecoder解码器，主要用于Protobuf的半包处理
                .addLast(new ProtobufVarint32FrameDecoder())
                /*添加ProtobufDecoder解码器，它的参数是com.google.protobuf.MessageLite，
               实际上就是告诉ProtobufDecoder需要解码的目标类是什么，
                   否则仅仅从字节数组中是无法判断出要解码的目标类型信息的（服务端需要解析的是客户端请求，所以是Request）*/
                .addLast(new ProtobufDecoder(InvocationPojo.Invocation.getDefaultInstance()))
                // 添加Protobuf...FieldPrepender编码器，主要用于Protobuf的半包处理
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                // 添加ProtobufEncoder编码器
                .addLast(new ProtobufEncoder())

                // 消息分发器
                .addLast(messageProtobufDispatcher)
                // 服务端处理器
                .addLast(nettyServerHandler)
        ;
    }

}
