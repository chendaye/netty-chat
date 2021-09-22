package top.chendaye666.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.chendaye666.common.dispatcher.MessageHandlerProtobufContainer;
import top.chendaye666.common.dispatcher.MessageProtobufDispatcher;

/**
 * 创建 NettyServerConfig 配置类，创建 MessageDispatcher 和 MessageHandlerContainer Bean
 */
@Configuration
public class NettyServerConfig {
    @Bean
    public MessageProtobufDispatcher messageProtobufDispatcher(){
        return new MessageProtobufDispatcher();
    }

    @Bean
    public MessageHandlerProtobufContainer messageHandlerProtobufContainer(){
        return new MessageHandlerProtobufContainer();
    }

}
