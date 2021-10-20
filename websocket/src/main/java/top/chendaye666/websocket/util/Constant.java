package top.chendaye666.websocket.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import top.chendaye666.websocket.model.po.GroupInfo;
import top.chendaye666.websocket.model.po.UserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: 全局常量
 *      1. USER_TOKEN 用户认证的键，用来匹配http session中的对应userId；
 *      2. webSocketServerHandshaker表，用channelId为键，存放握手实例。用来响应CloseWebSocketFrame的请求；
 *      3. onlineUser表，用userId为键，存放在线的客户端连接上下文；
 *      4. groupInfo表，用groupId为键，存放群信息；
 *      5. userInfo表，用username为键，存放用户信息。
 *      todo: 后期,有需要可以把上述信息存在redis中 或者 mysql 中
 * @author Chendaye666
 * @version 1.0
 */
public class Constant {
	// SER_TOKEN 用户认证的键，用来匹配http session中的对应userId
    public static final String USER_TOKEN = "userId";
    // <channelId, 握手实例> webSocketServerHandshaker表，用channelId为键，存放握手实例。用来响应CloseWebSocketFrame的请求
    public static Map<String, WebSocketServerHandshaker> webSocketHandshakerMap = 
            new ConcurrentHashMap<String, WebSocketServerHandshaker>();
    // <user, channel> onlineUser表，用userId为键，存放在线的客户端连接上下文
	public static Map<String, ChannelHandlerContext> onlineUserMap = 
	        new ConcurrentHashMap<String, ChannelHandlerContext>();
	// onlineUser表，用userId为键，存放在线的客户端连接上下文
	public static Map<String, UserInfo> userInfoMap =
			new HashMap<String, UserInfo>();
	//todo: 暂时没有群聊需求  (groupInfo表，用groupId为键，存放群信息)
	public static Map<String, GroupInfo> groupInfoMap =
	        new ConcurrentHashMap<String, GroupInfo>();

}
