package top.chendaye666.websocket.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chendaye666.websocket.annotation.LoginToken;
import top.chendaye666.websocket.annotation.PassToken;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.dao.UserMapper;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.IUserService;
import top.chendaye666.websocket.service.impl.UserServiceImpl;

import java.util.HashMap;

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
    public ServerResponse<String> register(@RequestBody User user){
        return iUserService.register(user);
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
    public ServerResponse<HashMap<String, Object>> login(String username, String password, boolean remember){
        return iUserService.login(username, password, remember);
    }

    /**
     * 登出
     * @return
     */
    @LoginToken
    @PostMapping("/logout")
    public ServerResponse<String> logout(Integer userId){
        return iUserService.logout(userId);
    }

    /**
     * 获取用户信息
     * @return
     */
    @LoginToken
    @GetMapping("/userinfo") // 大小写
    public ServerResponse<User> userInfo(int userId){
        return iUserService.getInformation(userId);
    }

    /**
     * 找回密码
     * @return
     */
    @PassToken
    @PostMapping("forgetpasswd")
    public ServerResponse<String> forgetPasswd(@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("passwordNew") String passwordNew){
        return iUserService.forgetResetPassword(username, email, passwordNew);
    }

    /**
     * 修改密码
     * @return
     */
    @PassToken
    @PostMapping("resetpasswd")
    public ServerResponse<String> resetPasswd(@RequestParam("userId") Integer userId, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword){
        return iUserService.resetPassword(userId, oldPassword, newPassword);
    }

    /**
     * 更新信息
     * @return
     */
    @PassToken
    @PostMapping("updateinfo")
    public ServerResponse<User> updateInfo(@RequestBody User user){
        return iUserService.updateInformation(user);
    }
}
