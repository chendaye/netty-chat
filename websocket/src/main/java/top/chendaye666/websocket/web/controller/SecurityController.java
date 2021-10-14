package top.chendaye666.websocket.web.controller;

import org.springframework.web.bind.annotation.*;
import top.chendaye666.websocket.annotation.PassToken;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

@RestController
public class SecurityController {

    @Autowired
    private SecurityService securityService;
    

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PassToken // 不需要鉴权
    public ServerResponse login(HttpSession session,
                                @RequestParam String username,
                                @RequestParam String password) {
        return securityService.login(username, password, session);
    }
    
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PassToken // 不需要鉴权
    public ServerResponse logout(HttpSession session) {
        return securityService.logout(session);
    }
}
