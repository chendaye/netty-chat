package top.chendaye666.common.dispatcher;

import io.netty.channel.Channel;

/**
 * 创建 MessageHandler 接口，消息处理器接口
 * @param <T>
 */
public interface MessageHandler<T extends Message> {

    /**
     * 执行处理消息
     *
     * @param channel 通道
     * @param message 消息
     */
    void execute(Channel channel, T message);

    /**
     * @return 消息类型，即每个 Message 实现类上的 TYPE 静态字段
     */
    String getType();

}
