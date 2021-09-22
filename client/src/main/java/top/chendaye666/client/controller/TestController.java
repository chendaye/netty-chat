package top.chendaye666.client.controller;

import top.chendaye666.client.client.NettyClient;
import top.chendaye666.common.codec.InvocationPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private NettyClient nettyClient;

    @PostMapping("/mock")
    public String mock(String type, String message) {
        // 创建 Invocation 对象
        InvocationPojo.Invocation invocation = InvocationPojo.Invocation.newBuilder()
                .setType(type)
                .setMessage(message)
                .build();
        // 发送消息
        nettyClient.send(invocation);
        return "success";
    }

}
