package top.chendaye666.server.server.handler;

import top.chendaye666.common.codec.InvocationDecoder;
import top.chendaye666.common.codec.InvocationEncoder;
import top.chendaye666.common.dispatcher.MessageDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Netty 的 ChannelHandler 组件，用来处理 Channel 的各种事件。这里的事件很广泛，比如可以是连接、数据读写、异常、数据转换等等。
 * ChannelHandler 有非常多的子类，其中有个非常特殊的 ChannelInitializer，它用于 Channel 创建时，实现自定义的初始化逻辑
 *
 * 在每一个客户端与服务端建立完成连接时，服务端会创建一个 Channel 与之对应。
 * 此时，NettyServerHandlerInitializer 会进行执行 #initChannel(Channel c) 方法，进行自定义的初始化。
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {

    /**
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 3 * 60;

    @Autowired
    private MessageDispatcher messageDispatcher;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    /*在 #initChannel(Channel ch) 方法的 ch 参数，就是此时创建的客户端 Channel*/
    @Override
    protected void initChannel(Channel ch) {
        // 获得 Channel 对应的 ChannelPipeline
        /*调用 Channel 的 #pipeline() 方法，获得客户端 Channel 对应的 ChannelPipeline。
        ChannelPipeline 由一系列的 ChannelHandler 组成，又或者说是 ChannelHandler 链。
        这样， Channel 所有上所有的事件都会经过 ChannelPipeline，被其上的 ChannelHandler 所处理
        就是一个 channel 上事件的处理链*/
        ChannelPipeline channelPipeline = ch.pipeline();
        // 添加一堆 NettyServerHandler 到 ChannelPipeline 中
        channelPipeline
                // 空闲检测
                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                // 编码器
                .addLast(new InvocationEncoder())
                // 解码器
                .addLast(new InvocationDecoder())
                // 消息分发器
                .addLast(messageDispatcher)
                // 服务端处理器
                .addLast(nettyServerHandler)
        ;
    }

}
