package top.chendaye666.client.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import top.chendaye666.client.client.handler.NettyClientHandler;
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
import top.chendaye666.common.dispatcher.MessageProtobufDispatcher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * API文档：https://netty.io/4.1/api/index.html
 */
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
    private Channel channel;

    /**
     * 连接池
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 60;

    @Autowired
    private MessageProtobufDispatcher messageProtobufDispatcher;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    /*连接池*/
    private FixedChannelPool fixedChannelPool;

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
                .option(ChannelOption.TCP_NODELAY, true); // 允许较小的数据包的发送，降低延迟,允许较小的数据包的发送，降低延迟

        //todo: 连接池 https://blog.csdn.net/a975261294/article/details/77568782
        // https://gist.github.com/ku27/522e2556d2f119e26319
        fixedChannelPool = new FixedChannelPool(bootstrap, new ChannelPoolHandler() {
            /*使用完channel需要释放才能放入连接池*/
            @Override
            public void channelReleased(Channel ch) throws Exception {
                // TODO Auto-generated method stub
                // 刷新管道里的数据
                ch.writeAndFlush(Unpooled.EMPTY_BUFFER); //flush掉所有写回的数据
            }
            /*获取连接池中的channel*/
            @Override
            public void channelAcquired(Channel ch) throws Exception {

            }
            /* 当channel不足时会创建，但不会超过限制的最大channel数*/
            @Override
            public void channelCreated(Channel ch) throws Exception {
                // TODO Auto-generated method stub
                NioSocketChannel channel = (NioSocketChannel) ch;
                // 客户端逻辑处理   ClientHandler这个也是咱们自己编写的，继承ChannelInboundHandlerAdapter，实现你自己的逻辑
                channel.pipeline()
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
                        // 消息分发（处理）
                        .addLast(messageProtobufDispatcher)
                        // 客户端处理器
                        .addLast(nettyClientHandler);

            }
        }, 6);
        // 从连接池中获取 channel
        fixedChannelPool.acquire().addListener(
                new FutureListener<Channel>(){
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        if (!future.isSuccess()){
                            logger.error("[start][Netty Client 连接服务器({}:{}) 失败]", serverHost, serverPort);
                            reconnect(); // 重连
                            return;
                        }
                        // 获取channel
                        channel = future.getNow();
                        logger.info("[start][Netty Client 连接服务器({}:{}) 成功]", serverHost, serverPort);
                    }
                }
        );
    }

    /**
     * 重连
     */
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
        // 关闭连接池
        fixedChannelPool.close();
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
        // channel 放回连接池(写完数据)
        fixedChannelPool.release(channel);
    }
}
