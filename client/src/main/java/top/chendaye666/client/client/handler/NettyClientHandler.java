package top.chendaye666.client.client.handler;

import top.chendaye666.client.client.NettyClient;
import top.chendaye666.client.message.heartbeat.HeartbeatRequest;
import top.chendaye666.common.codec.Invocation;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable // 在类上添加 @ChannelHandler.Sharable 注解，标记这个 ChannelHandler 可以被多个 Channel 使用。
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NettyClient nettyClient;

    /**
     * #channelInactive(ChannelHandlerContext ctx) 方法，实现在和服务端断开连接时，
     * 调用 NettyClient 的 #reconnect() 方法，实现客户端定时和服务端重连
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 发起重连
        nettyClient.reconnect();
        // 继续触发事件
        super.channelInactive(ctx);
    }

    /**
     * #exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 方法，在处理 Channel 的事件发生异常时，
     * 调用 Channel 的 #close() 方法，断开和客户端的连接。
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("[exceptionCaught][连接({}) 发生异常]", ctx.channel().id(), cause);
        // 断开连接
        ctx.channel().close();
    }

    /**
     * #userEventTriggered(ChannelHandlerContext ctx, Object event) 方法，在客户端在空闲时，向服务端发送一次心跳，即心跳机制
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        // 空闲时，向服务端发起一次心跳
        if (event instanceof IdleStateEvent) {
            logger.info("[userEventTriggered][发起一次心跳]");
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            ctx.writeAndFlush(new Invocation(HeartbeatRequest.TYPE, heartbeatRequest))
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, event);
        }
    }

}
