package top.chendaye666.client.config;

import top.chendaye666.common.dispatcher.MessageHandlerProtobufContainer;
import top.chendaye666.common.dispatcher.MessageProtobufDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建 NettyClientConfig 配置类，创建 MessageDispatcher 和 MessageHandlerContainer Bean
 */
@Configuration
public class NettyClientConfig {

    @Bean
    public MessageProtobufDispatcher messageProtobufDispatcher(){
        return new MessageProtobufDispatcher();
    }

    @Bean
    public MessageHandlerProtobufContainer messageHandlerProtobufContainer(){
        return new MessageHandlerProtobufContainer();
    }

}
