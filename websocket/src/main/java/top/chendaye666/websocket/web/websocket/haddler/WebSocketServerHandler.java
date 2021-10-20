package top.chendaye666.websocket.web.websocket.haddler;


import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import top.chendaye666.websocket.Exception.BizException;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.service.ChatService;
import top.chendaye666.websocket.service.JWTService;
import top.chendaye666.websocket.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 处理 websocket 消息
 */
@Component
@Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);
    
    @Autowired
    private ChatService chatService;

    @Autowired
    private JWTService jwtService;

    /**
     * 描述：读取完连接的消息后，对消息进行处理。
     *      这里主要是处理WebSocket请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        handlerWebSocketFrame(ctx, msg);
    }
    
    /**
     * 描述：处理WebSocketFrame
     * @param ctx
     * @param frame
     * @throws Exception
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker = 
                    Constant.webSocketHandshakerMap.get(ctx.channel().id().asLongText());
            if (handshaker == null) {
                sendErrorMessage(ctx, "不存在的客户端连接！");
            } else {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        // ping请求
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 只支持文本格式，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            sendErrorMessage(ctx, "仅支持文本(Text)格式，不支持二进制消息");
        }

        // 客服端发送过来的消息
        String request = ((TextWebSocketFrame)frame).text();
        LOGGER.info("服务端收到新信息：" + request);
        JSONObject param = null;
        try {
            // 消息解析为json
            param = JSONObject.parseObject(request);
            System.out.println(param.toString());
        } catch (Exception e) {
            sendErrorMessage(ctx, "JSON字符串转换出错！");
            e.printStackTrace();
        }
        if (param == null) {
            sendErrorMessage(ctx, "参数为空！");
            return;
        }

        //todo： token 验证
        validToken(ctx, param);
        //todo: 消息分发
        String type = (String) param.get("type");
        switch (type) {
            case "ROBOT":
                chatService.robot(param, ctx); // 机器人聊天
                break;
            case "REGISTER": // 聊天注册
                chatService.register(param, ctx);
                break;
            case "SINGLE_SENDING": // 单聊
                chatService.singleSend(param, ctx);
                break;
            case "GROUP_SENDING": // 群聊
                chatService.groupSend(param, ctx);
                break;
            case "FILE_MSG_SINGLE_SENDING": // 文件单发
                chatService.FileMsgSingleSend(param, ctx);
                break;
            case "FILE_MSG_GROUP_SENDING": // 文件群发
                chatService.FileMsgGroupSend(param, ctx);
                break;
            default:
                chatService.typeError(ctx);
                break;
        }
    }

    /**
     * token 验证
     */
    public void validToken(ChannelHandlerContext ctx, JSONObject param){
        try {
            // 获取当前要注册聊天的用户 token
            String token = (String)param.get("token");
            DecodedJWT jwt = jwtService.verifyToken(token);
        }catch (Exception e){
            sendErrorMessage(ctx, "token 验证失败！");
            throw new BizException("token 验证失败！");
        }
    }
    
    /**
     * 描述：客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        chatService.remove(ctx);
    }
   
    /**
     * 异常处理：关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    
    private void sendErrorMessage(ChannelHandlerContext ctx, String errorMsg) {
        String responseJson = ServerResponse.createByErrorMessage(errorMsg).toString();
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
    }

}
