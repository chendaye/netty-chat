# 参考
[芋道 Spring Boot Netty 入门](https://www.iocoder.cn/Spring-Boot/Netty/?github#)

[Netty 解决粘包和拆包问题的四种方案](https://www.iocoder.cn/Fight/Netty-to-solve-the-problem-of-sticky-and-unpacked-four-solutions/?self)

[TCP Keepalive 机制刨根问底](https://www.iocoder.cn/Fight/TCP-Keepalive-%E6%9C%BA%E5%88%B6%E5%88%A8%E6%A0%B9%E9%97%AE%E5%BA%95/?self)


[Netty Bootstrap（图解）](https://www.iocoder.cn/Fight/crazymakercircle/Netty-Bootstrap/?self)

[Netty 实现原理与源码解析系统 —— 精品合集](https://www.iocoder.cn/Netty/Netty-collection/?self)


[Netty编解码方案之Protobuf介绍](https://cloud.tencent.com/developer/article/1579441)

[Netty——Protobuf编解码](https://www.cnblogs.com/caoweixiong/p/14684453.html)
[netty 4.1.45 protobuf 编解码实现](https://blog.csdn.net/liubenlong007/article/details/104231927)

[\【Protobuf专题】（一）基于IDEA实现Proto一站式编辑及编译](https://segmentfault.com/a/1190000038778590)

> 在IDEA中安装插件。包括GenProtobuf和Protocol Buffer Editor，前者用于一键转换proto文件，后者用于编辑proto文件（未安装前，IDEA不支持对proto语法，没有高亮显示和自动补全提示）



[「IDEA插件精选」安利一个IDEA骚操作:一键生成方法的序列图](https://juejin.cn/post/6887719053931053064)

> 通过 SequenceDiagram 这个插件，我们一键可以生成时序图。

- 点击时序图中的类/方法即可跳转到对应的地方。
- 从时序图中删除对应的类或者方法。
- 将生成的时序图导出为 PNG 图片格式。

# Netty 的理解

> 实现 TCP协议

- server 端
- 编码解码器
- 连接 channel  
- client 端


[彻底理解Netty，这一篇文章就够了](https://juejin.cn/post/6844903703183360008)

# Protobuf与netty结合

> 在 Netty 数据传输过程中可以有很多选择，比如；字符串、json、xml、java 对象，但为了保证传输的数据具备；良好的通用性、方便的操作性和传输的高性能，
> 我们可以选择 protobuf 作为我们的数据传输格式。

> Netty为protobuf提供了两个编码器（ProtobufVarint32LengthFieldPrepender、ProtobufEncoder），
> 两个解码器（ProtobufVarint32FrameDecoder、ProtobufDecoder）。

> 写一个使用Protobuf作为序列化框架，Netty作为传输层的最简单的demo，需求描述：


# client 连接池

[netty client连接池设计](https://zhuanlan.zhihu.com/p/33805654)

[FixedChannelPool](https://juejin.cn/post/6844903433862905869)