package top.chendaye666.websocket.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chendaye666.websocket.annotation.LoginToken;
import top.chendaye666.websocket.annotation.PassToken;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.dao.UserMapper;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.IUserService;
import top.chendaye666.websocket.service.impl.UserServiceImpl;

/**
 * 用户相关的接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 注册新用户
     * @return
     */
    @PassToken // 不需要鉴权
    @PostMapping("/register")
    public ServerResponse<String> register(User user){
        return ServerResponse.createBySuccess();
    }

    /**
     * 检查是否重名
     * @return
     */
    @LoginToken
    @GetMapping("checkvalid")
    public ServerResponse<String> checkValid(String field, String type){
        return iUserService.checkValid(field, type);
    }

    /**
     * 用户登陆
     * @return
     */
    @PassToken(required = true) // 不需要鉴权
    @PostMapping("/login")
    public ServerResponse<String> login(){
        return ServerResponse.createBySuccess();
    }

    /**
     * 登出
     * @return
     */
    @LoginToken
    @PostMapping("/logout")
    public ServerResponse<String> logout(){
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取用户信息
     * @return
     */
    @LoginToken
    @GetMapping("/userinfo")
    public ServerResponse<String> userInfo(){
        return ServerResponse.createBySuccess();
    }

    /**
     * 找回密码
     * @return
     */
    @PassToken
    @PostMapping("forgetpasswd")
    public ServerResponse<String> forgetPasswd(){
        return ServerResponse.createBySuccess();
    }

    /**
     * 修改密码
     * @return
     */
    @PassToken
    @PostMapping("resetpasswd")
    public ServerResponse<String> resetPasswd(){
        return ServerResponse.createBySuccess();
    }

    /**
     * 更新信息
     * @return
     */
    @PassToken
    @PostMapping("updateinfo")
    public ServerResponse<String> updateInfo(){
        return ServerResponse.createBySuccess();
    }
}
