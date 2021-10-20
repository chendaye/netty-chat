package top.chendaye666.websocket.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

/**
 * 聊天服务
 */
public interface ChatService {

    public void register(JSONObject param, ChannelHandlerContext ctx);
    
    public void robot(JSONObject param, ChannelHandlerContext ctx);

    public void singleSend(JSONObject param, ChannelHandlerContext ctx);

    public void groupSend(JSONObject param, ChannelHandlerContext ctx);
    
    public void FileMsgSingleSend(JSONObject param, ChannelHandlerContext ctx);
    
    public void FileMsgGroupSend(JSONObject param, ChannelHandlerContext ctx);
    
    public void remove(ChannelHandlerContext ctx);
    
    public void typeError(ChannelHandlerContext ctx);
        
}
