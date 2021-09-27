package top.chendaye666.client.client.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.Attribute;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * channel 现成池， 最大线程数默认 4
 */
public class NettyChannelPool {
    private Channel[] channels;
    private Object [] locks;
    private static final int MAX_CHANNEL_COUNT = 4;

    public NettyChannelPool() {
        // 线程
        this.channels = new Channel[MAX_CHANNEL_COUNT];
        // 锁
        this.locks = new Object[MAX_CHANNEL_COUNT];
        for (int i = 0; i < MAX_CHANNEL_COUNT; i++) {
            this.locks[i] = new Object();
        }
    }

    /**
     * 从线程池同步获取netty channel
     */
    public Channel syncGetChannel() throws InterruptedException {
        //产生一个随机数,随机的从数组中获取channel
        int index = new Random().nextInt(MAX_CHANNEL_COUNT);
        Channel channel = channels[index];
        //如果能获取到,直接返回
        if (channel != null && channel.isActive()) {
            return channel;
        }
        // 如果没有获取到（有其他线程获取到了锁 ：locks[index]）
        synchronized (locks[index]) {
            // 拿到锁，获取 channel
            channel = channels[index];
            //这里必须再次做判断,当锁被释放后，之前等待的线程已经可以直接拿到结果了。
            if (channel != null && channel.isActive()) {
                return channel;
            }
            // 如果拿到的 channel 无效，开始跟服务端交互，新创建一个 channel，并放入线程池
            channel = connectToServer();
            channels[index] = channel;
        }
        return channel;
    }

    /**
     * channel 连接server
     * @return
     * @throws InterruptedException
     */
    private Channel connectToServer() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new SelfDefineEncodeHandler());
                        pipeline.addLast(new SocketClientHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("localhost", 8899);
        Channel channel = channelFuture.sync().channel();

        //为刚刚创建的channel，初始化channel属性 :Map<消息序列号, 消息回调函数>
        Attribute<Map<Integer,Object>> attribute = channel.attr(ChannelUtils.DATA_MAP_ATTRIBUTEKEY);
        ConcurrentHashMap<Integer, Object> dataMap = new ConcurrentHashMap<>();
        attribute.set(dataMap);
        return channel;
    }
}