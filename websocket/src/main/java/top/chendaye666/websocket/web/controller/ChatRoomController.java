package top.chendaye666.websocket.web.controller;


import org.springframework.web.bind.annotation.RestController;
import top.chendaye666.websocket.model.vo.ResponseJson;
import top.chendaye666.websocket.service.UserInfoService;
import top.chendaye666.websocket.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public ResponseJson getUserInfo(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        return userInfoService.getByUserId((String)userId);
    }
}
