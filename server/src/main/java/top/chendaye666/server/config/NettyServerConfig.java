package top.chendaye666.server.config;

import top.chendaye666.common.dispatcher.MessageDispatcher;
import top.chendaye666.common.dispatcher.MessageHandlerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建 NettyServerConfig 配置类，创建 MessageDispatcher 和 MessageHandlerContainer Bean
 */
@Configuration
public class NettyServerConfig {

    @Bean
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

    @Bean
    public MessageHandlerContainer messageHandlerContainer() {
        return new MessageHandlerContainer();
    }

}
