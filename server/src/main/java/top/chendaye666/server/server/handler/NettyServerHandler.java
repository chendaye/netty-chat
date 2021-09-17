package top.chendaye666.server.server.handler;

import top.chendaye666.server.server.NettyChannelManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端 Channel 实现类，提供对客户端 Channel 建立连接、断开连接、异常时的处理
 */
@Component
@ChannelHandler.Sharable // @ChannelHandler.Sharable 注解，标记这个 ChannelHandler 可以被多个 Channel 使用
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /*channelManager 属性，是我们实现的客户端 Channel 的管理器。
    * */
    @Autowired
    private NettyChannelManager channelManager;

    /**
     * #channelActive(ChannelHandlerContext ctx) 方法，在客户端和服务端建立连接完成时，
     * 调用 NettyChannelManager 的 #add(Channel channel) 方法，添加到其中。
     *
     * 添加 channel
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 从管理器中添加
        channelManager.add(ctx.channel());
    }

    /**
     * #channelUnregistered(ChannelHandlerContext ctx) 方法，在客户端和服务端断开连接时，
     * 调用 NettyChannelManager 的 #add(Channel channel) 方法，从其中移除。
     *
     * 移除 channel
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        // 从管理器中移除
        channelManager.remove(ctx.channel());
    }

    /**
     * #exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 方法，
     * 在处理 Channel 的事件发生异常时，调用 Channel 的 #close() 方法，断开和客户端的连接
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("[exceptionCaught][连接({}) 发生异常]", ctx.channel().id(), cause);
        // 断开连接
        ctx.channel().close();
    }

}
