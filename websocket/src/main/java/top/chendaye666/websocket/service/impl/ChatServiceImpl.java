package top.chendaye666.websocket.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.mock.GroupInfoDao;
import top.chendaye666.websocket.model.po.GroupInfo;
import top.chendaye666.websocket.service.ChatService;
import top.chendaye666.websocket.util.ChatType;
import top.chendaye666.websocket.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@Service
public class ChatServiceImpl implements ChatService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
            
    @Autowired
    private GroupInfoDao groupDao;

    /**
     * 上线注册
     * {"content":"content","type":"REGISTER","status":200,"userId":"001"}
     * @param param
     * @param ctx
     */
    @Override
    public void register(JSONObject param, ChannelHandlerContext ctx) {
        String userId = (String)param.get("userId");
        //todo: 注册 <user,channel>
        Constant.onlineUserMap.put(userId, ctx);
        HashMap<String, ChatType> data = new HashMap<>();
        data.put("type", ChatType.REGISTER);
        String responseJson = ServerResponse.createBySuccess(data).toString();
        sendMessage(ctx, responseJson);
        LOGGER.info(MessageFormat.format("userId为 {0} 的用户登记到在线用户表，当前在线人数为：{1}"
                , userId, Constant.onlineUserMap.size()));
    }

    /**
     * 单聊
     * 消息格式：{"content":"content","type":"SINGLE_SENDING","status":200,"fromUserId":"001", "toUserId":"002"}
     * @param param
     * @param ctx
     */
    @Override
    public void singleSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String)param.get("fromUserId");
        String toUserId = (String)param.get("toUserId");
        String content = (String)param.get("content");
        ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(toUserId);
        if (toUserCtx == null) {
            String responseJson = ServerResponse.createByErrorMessage(MessageFormat.format("userId为 {0} 的用户没有登录！", toUserId)).toString();
            sendMessage(ctx, responseJson);
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("fromUserId", fromUserId);
            data.put("content", content);
            data.put("content", content);
            data.put("type", ChatType.SINGLE_SENDING.toString());
            String responseJson = ServerResponse.createBySuccess(data).toString();
            sendMessage(toUserCtx, responseJson);
        }
    }

    /**
     * 群聊
     * @param param
     * @param ctx
     */
    @Override
    public void groupSend(JSONObject param, ChannelHandlerContext ctx) {
        
        String fromUserId = (String)param.get("fromUserId");
        String toGroupId = (String)param.get("toGroupId");
        String content = (String)param.get("content");

        GroupInfo groupInfo = groupDao.getByGroupId(toGroupId);
        if (groupInfo == null) {
            String responseJson = ServerResponse.createByErrorMessage("该群id不存在").toString();
            sendMessage(ctx, responseJson);
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("fromUserId", fromUserId);
            data.put("content", content);
            data.put("toGroupId", toGroupId);
            data.put("type", ChatType.GROUP_SENDING.toString());
            String responseJson = ServerResponse.createBySuccess(data).toString();
            groupInfo.getMembers().stream()
                .forEach(member -> { 
                    ChannelHandlerContext toCtx = Constant.onlineUserMap.get(member.getUserId());
                    if (toCtx != null && !member.getUserId().equals(fromUserId)) {
                        sendMessage(toCtx, responseJson);
                    }
                });
        }
    }

    /**
     * 下线
     * @param ctx
     */
    @Override
    public void remove(ChannelHandlerContext ctx) {
        Iterator<Entry<String, ChannelHandlerContext>> iterator = 
                Constant.onlineUserMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, ChannelHandlerContext> entry = iterator.next();
            if (entry.getValue() == ctx) {
                LOGGER.info("正在移除握手实例...");
                Constant.webSocketHandshakerMap.remove(ctx.channel().id().asLongText());
                LOGGER.info(MessageFormat.format("已移除握手实例，当前握手实例总数为：{0}"
                        , Constant.webSocketHandshakerMap.size()));
                iterator.remove();
                LOGGER.info(MessageFormat.format("userId为 {0} 的用户已退出聊天，当前在线人数为：{1}"
                        , entry.getKey(), Constant.onlineUserMap.size()));
                break;
            }
        }
    }

    /**
     * 单聊文件发送
     * @param param
     * @param ctx
     */
    @Override
    public void FileMsgSingleSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String)param.get("fromUserId");
        String toUserId = (String)param.get("toUserId");
        String originalFilename = (String)param.get("originalFilename");
        String fileSize = (String)param.get("fileSize");
        String fileUrl = (String)param.get("fileUrl");
        ChannelHandlerContext toUserCtx = Constant.onlineUserMap.get(toUserId);
        if (toUserCtx == null) {
            String responseJson = ServerResponse.createByErrorMessage(MessageFormat.format("userId为 {0} 的用户没有登录！", toUserId)).toString();
            sendMessage(ctx, responseJson);
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("fromUserId", fromUserId);
            data.put("originalFilename", originalFilename);
            data.put("fileSize", fileSize);
            data.put("fileUrl", fileUrl);
            data.put("type", ChatType.FILE_MSG_SINGLE_SENDING.toString());
            String responseJson = ServerResponse.createBySuccess(data).toString();
            sendMessage(toUserCtx, responseJson);
        }
    }

    /**
     * 群聊文件发送
     * @param param
     * @param ctx
     */
    @Override
    public void FileMsgGroupSend(JSONObject param, ChannelHandlerContext ctx) {
        String fromUserId = (String)param.get("fromUserId");
        String toGroupId = (String)param.get("toGroupId");
        String originalFilename = (String)param.get("originalFilename");
        String fileSize = (String)param.get("fileSize");
        String fileUrl = (String)param.get("fileUrl");
        GroupInfo groupInfo = groupDao.getByGroupId(toGroupId);
        if (groupInfo == null) {
            String responseJson  = ServerResponse.createByErrorMessage("该群id不存在").toString();
            sendMessage(ctx, responseJson);
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("fromUserId", fromUserId);
            data.put("toGroupId", toGroupId);
            data.put("originalFilename", originalFilename);
            data.put("fileSize", fileSize);
            data.put("fileUrl", fileUrl);
            data.put("type", ChatType.FILE_MSG_GROUP_SENDING.toString());
            String responseJson = ServerResponse.createBySuccess(data).toString();
            groupInfo.getMembers().stream()
                .forEach(member -> {
                    //todo: 把消息发送给谁
                    ChannelHandlerContext toCtx = Constant.onlineUserMap.get(member.getUserId());
                    if (toCtx != null && !member.getUserId().equals(fromUserId)) {
                        sendMessage(toCtx, responseJson);
                    }
                });
        }
    }

    /**
     * 聊天消息错误
     * @param ctx
     */
    @Override
    public void typeError(ChannelHandlerContext ctx) {
        String responseJson = ServerResponse.createByErrorMessage("该类型不存在！").toString();
        sendMessage(ctx, responseJson);
    }

    /**
     * 发送消息
     * @param ctx
     * @param message
     */
    private void sendMessage(ChannelHandlerContext ctx, String message) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    }
}
