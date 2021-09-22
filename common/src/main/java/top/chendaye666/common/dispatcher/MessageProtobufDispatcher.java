package top.chendaye666.common.dispatcher;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import top.chendaye666.common.codec.InvocationPojo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在类上添加 @ChannelHandler.Sharable 注解，标记这个 ChannelHandler 可以被多个 Channel 使用
 *
 *  SimpleChannelInboundHandler 是 Netty 定义的消息处理 ChannelHandler 抽象类，处理消息的类型是 <I> 泛型时
 *
 *  #channelRead0(ChannelHandlerContext ctx, Invocation invocation) 方法，处理消息，进行分发
 *
 *
 *
 *
 * 调用 MessageHandlerContainer 的 #getMessageHandler(String type) 方法，
 * 获得 Invocation 的 type 对应的 MessageHandler 处理器。
 *
 * 然后，调用 MessageHandlerContainer 的 #getMessageClass(messageHandler) 方法，获得 MessageHandler 处理器的消息类。
 *
 * 调用 JSON 的 # parseObject(String text, Class<T> clazz) 方法，将 Invocation 的 message 解析成 MessageHandler 对应的消息对象。
 *
 * 丢到线程池中，然后调用 MessageHandler 的 #execute(Channel channel, T message) 方法，执行业务逻辑。
 */
@ChannelHandler.Sharable
public class MessageProtobufDispatcher extends SimpleChannelInboundHandler<InvocationPojo.Invocation> {

    @Autowired
    private MessageHandlerProtobufContainer messageHandlerProtobufContainer;
    /*为什么要丢到 executor 线程池中呢？
    * 启动 Netty 服务端或者客户端时，都会设置其 EventGroup
    *
    * EventGroup 我们可以先简单理解成一个线程池，并且线程池的大小仅仅是 CPU 数量 * 2。
    * 每个 Channel 仅仅会被分配到其中的一个线程上，进行数据的读写。并且，多个 Channel 会共享一个线程，即使用同一个线程进行数据的读写。
    *
    * MessageHandler 的具体逻辑视线中，往往会涉及到 IO 处理，例如说进行数据库的读取。
    * 这样，就会导致一个 Channel 在执行 MessageHandler 的过程中，阻塞了共享当前线程的其它 Channel 的数据读取。
    *
    * 这里创建了 executor 线程池，进行 MessageHandler 的逻辑执行，避免阻塞 Channel 的数据读取
    * */
    private final ExecutorService executor =  Executors.newFixedThreadPool(200);

    /**
     * 根据每一条消息（Invocation）的 type， 匹配对应的处理逻辑 （messageHandler）
     * @param ctx
     * @param invocation
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvocationPojo.Invocation invocation) {
        // 获得 type 对应的 MessageHandler 处理器  type=AUTH_REQUEST
        MessageHandler messageHandler = messageHandlerProtobufContainer.getMessageHandler(invocation.getType());
        // 获得  MessageHandler 处理器 的消息类（也就是要把当前消息中的json 信息，解析成何种对象）
        Class<? extends Message> messageClass = MessageHandlerProtobufContainer.getMessageClass(messageHandler);
        // 解析消息 invocation中的message 解析成对应的 对象
        Message message = JSON.parseObject(invocation.getMessage(), messageClass);
        // 执行逻辑
        executor.submit(new Runnable() {

            @Override
            public void run() {
                // noinspection unchecked，处理逻辑
                messageHandler.execute(ctx.channel(), message);
            }

        });
    }

}
