# 参考

[https://gitee.com/AZHELL/netty-chat-room?_from=gitee_search](https://gitee.com/AZHELL/netty-chat-room?_from=gitee_search)

[https://github.com/Chendaye666vogels/Chatroom](https://github.com/Chendaye666vogels/Chatroom)


# 操作

## rest 操作

```bash
# 用户注册
localhost:7777/user/register

{
	"username": "netty03",
	"password": "netty03",
	"email": "netty03@163.com",
	"avatar": "localhost:7777/user/register/register",
	"role": 2,
	"phone": "15271834241"
}

# 用户登陆
localhost:7777/user/login
username netty01
password netty01
remember true

# 用户更新
localhost:7777/user/updateinfo
{
	"id":4,
    "username": "long666777",
	"password": "chendaye666",
	"email": "152718342412@163.com",
	"avatar": "localhost:7777/user/register",
	"role": 2,
	"phone": "15271834241"
}

# 用户信息
localhost:7777/user/userinfo?userId=7
token ****
```


## websocket 操作

> 连接 websocket: `ws://localhost:3333`

> 注意：用户在同一个websocket 界面，注册+发送消息
```bash
# 上线注册
{"content":"content","type":"REGISTER","status":200,"token":"xxx"}

# 单聊
{"content":"content","type":"SINGLE_SENDING","status":200,"fromUserToken":"xxx", "toUserToken":"xxx"}

```
# 运行

```bash
nohup java -jar websocket-1.0-SNAPSHOT.jar 1>/dev/null 2>&1 &
```

# 打包问题

> 如果在IDEA本地可以运行，打包之后不能运行，就是打包插件配置的问题

> 要注意父子项目打包配置的覆盖问题 



