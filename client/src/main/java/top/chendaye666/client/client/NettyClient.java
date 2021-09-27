package top.chendaye666.client.client;

import top.chendaye666.client.client.handler.NettyClientHandlerInitializer;
import top.chendaye666.common.codec.InvocationPojo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component // 在类上，添加 @Component 注解，把 NettyClient 的创建交给 Spring 管理。
public class NettyClient {

    /**
     * 重连频率，单位：秒
     */
    private static final Integer RECONNECT_SECONDS = 20;

    private Logger logger = LoggerFactory.getLogger(getClass());

    // serverHost 和 serverPort 属性，读取 application.yml 配置文件的 netty.server.host 和 netty.server.port 配置项
    @Value("${netty.server.host}")
    private String serverHost;
    @Value("${netty.server.port}")
    private Integer serverPort;

    @Autowired
    private NettyClientHandlerInitializer nettyClientHandlerInitializer;

    /**
     * 线程组，用于客户端对服务端的链接、数据读写
     */
    private EventLoopGroup eventGroup = new NioEventLoopGroup();
    /**
     * Netty Client Channel
     */
    private volatile Channel channel;

    /**
     * 启动 Netty Client
     * #start() 方法，添加 @PostConstruct 注解，启动 Netty 客户端
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 创建 Bootstrap 对象，用于 Netty Client 启动
        /*创建 Bootstrap 类，Netty 提供的客户端的启动类，方便我们初始化 Client*/
        Bootstrap bootstrap = new Bootstrap();
        // 设置 Bootstrap 的各种属性。
        bootstrap.group(eventGroup) // 设置一个 EventLoopGroup 对象,设置使用 eventGroup 线程组，实现客户端对服务端的连接、数据读写
                .channel(NioSocketChannel.class)  // 指定 Channel 为客户端 NioSocketChannel,设置使用 NioSocketChannel 类，它是 Netty 定义的 NIO 服务端 TCP Client 实现类。
                .remoteAddress(serverHost, serverPort) // 指定链接服务器的地址,置连接服务端的地址
                .option(ChannelOption.SO_KEEPALIVE, true) // TCP Keepalive 机制，实现 TCP 层级的心跳保活功能,TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .option(ChannelOption.TCP_NODELAY, true) // 允许较小的数据包的发送，降低延迟,允许较小的数据包的发送，降低延迟
                .handler(nettyClientHandlerInitializer); // 设置自己 Channel 的处理器为 NettyClientHandlerInitializer
        // 链接服务器，并异步等待成功，即启动客户端
        /*调用 #connect() 方法，连接服务器，并异步等待成功，即启动客户端。同时，
        添加回调监听器 ChannelFutureListener，在连接服务端失败的时候，调用 #reconnect() 方法，实现定时重连*/
        bootstrap.connect().addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 连接失败
                if (!future.isSuccess()) {
                    logger.error("[start][Netty Client 连接服务器({}:{}) 失败]", serverHost, serverPort);
                    reconnect();
                    return;
                }
                //todo： 连接成功, 获取 channel
                channel = future.channel();
                logger.info("[start][Netty Client 连接服务器({}:{}) 成功]", serverHost, serverPort);
            }

        });
    }

    public void reconnect() {
        eventGroup.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("[reconnect][开始重连]");
                try {
                    start();
                } catch (InterruptedException e) {
                    logger.error("[reconnect][重连失败]", e);
                }
            }
        }, RECONNECT_SECONDS, TimeUnit.SECONDS);
        logger.info("[reconnect][{} 秒后将发起重连]", RECONNECT_SECONDS);
    }

    /**
     * 关闭 Netty Server
     * #shutdown() 方法，添加 @PreDestroy 注解，关闭 Netty 客户端
     *
     * 调用 Channel 的 #close() 方法，关闭 Netty Client，这样客户端就断开和服务端的连接
     */
    @PreDestroy
    public void shutdown() {
        // 关闭 Netty Client
        if (channel != null) {
            channel.close();
        }
        // 优雅关闭一个 EventLoopGroup 对象.例如说，它们里面的线程池。
        eventGroup.shutdownGracefully();
    }

    /**
     * 发送消息
     * 因为 NettyClient 是客户端，所以无需像 NettyServer 一样使用「2.1.4 NettyChannelManager」维护 Channel 的集合
     *
     * @param invocation 消息体
     */
    public void send(InvocationPojo.Invocation invocation) {
        if (channel == null) {
            logger.error("[send][连接不存在]");
            return;
        }
        if (!channel.isActive()) {
            logger.error("[send][连接({})未激活]", channel.id());
            return;
        }
        // 发送消息
        channel.writeAndFlush(invocation);
    }

}
