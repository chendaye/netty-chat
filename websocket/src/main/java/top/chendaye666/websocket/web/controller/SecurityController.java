package top.chendaye666.websocket.web.controller;

import org.springframework.web.bind.annotation.*;
import top.chendaye666.websocket.model.vo.ResponseJson;
import top.chendaye666.websocket.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@RestController
public class SecurityController {

    @Autowired
    private SecurityService securityService;
    

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson login(HttpSession session,
            @RequestParam String username,
            @RequestParam String password) {
        return securityService.login(username, password, session);
    }
    
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseJson logout(HttpSession session) {
        return securityService.logout(session);
    }
}
