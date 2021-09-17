package top.chendaye666.server.server;

import top.chendaye666.server.server.handler.NettyServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * 初始化并启动Netty 服务端
 */
@Component // 添加 @Component 注解，把 NettyServer 的创建交给 Spring 管理
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 配置端口
    @Value("${netty.port}")
    private Integer port;

    @Autowired
    private NettyServerHandlerInitializer nettyServerHandlerInitializer;

    /**
     * boss 线程组，用于服务端接受客户端的连接
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * worker 线程组，用于服务端接受客户端的数据读写
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * Netty Server Channel
     */
    private Channel channel;

    /**
     * 启动 Netty Server
     *
     * 被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
     * PostConstruct在构造函数之后执行，init（）方法之前执行。
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 创建 ServerBootstrap 对象，用于 Netty Server 启动
        /*创建 ServerBootstrap 类，Netty 提供的服务器的启动类，方便我们初始化 Server*/
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置 ServerBootstrap 的各种属性
        /*bossGroup 属性：Boss 线程组，用于服务端接受客户端的连接*/
        /*workerGroup 属性：Worker 线程组，用于服务端接受客户端的数据读写*/
        bootstrap.group(bossGroup, workerGroup) // 设置两个 EventLoopGroup 对象
            /*Netty 定义的 NIO 服务端 TCP Socket 实现类*/
                .channel(NioServerSocketChannel.class)  // 指定 Channel 为服务端 NioServerSocketChannel
                .localAddress(new InetSocketAddress(port)) // 设置 Netty Server 的端口
            /*因为 TCP 建立连接是三次握手，所以第一次握手完成后，会添加到服务端的连接队列中。*/
                .option(ChannelOption.SO_BACKLOG, 1024) // 服务端 accept 队列的大小
                .childOption(ChannelOption.SO_KEEPALIVE, true) // TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .childOption(ChannelOption.TCP_NODELAY, true) // 允许较小的数据包的发送，降低延迟
                .childHandler(nettyServerHandlerInitializer); // 设置客户端连接上来的 Channel 的处理器为 NettyServerHandlerInitializer
        // 绑定端口，并同步等待成功，即启动服务端
        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            channel = future.channel();
            logger.info("[start][Netty Server 启动在 {} 端口]", port);
        }
    }

    /**
     * 关闭 Netty Server
     *
     * PreDestroy（）方法在destroy（）方法执行之后执行
     */
    @PreDestroy
    public void shutdown() {
        // 关闭 Netty Server
        if (channel != null) {
            channel.close(); // 关闭 Netty Server，这样客户端就不再能连接了。
        }
        // 优雅关闭两个 EventLoopGroup 对象
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
