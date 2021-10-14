package top.chendaye666.websocket.web.controller;


import org.springframework.web.bind.annotation.RestController;
import top.chendaye666.websocket.annotation.LoginToken;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.service.UserInfoService;
import top.chendaye666.websocket.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/chatroom")
public class ChatRoomController {

    @Autowired
    private UserInfoService userInfoService;
    

    /**
     * 描述：登录成功跳转页面后，调用此接口获取用户信息
     * @return
     */
    @RequestMapping(value = "/get_userinfo", method = RequestMethod.POST) 
    @ResponseBody
    @LoginToken // 需要鉴权
    public ServerResponse getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String)userId);
    }
}
