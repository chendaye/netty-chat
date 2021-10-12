package top.chendaye666.websocket.service;

import top.chendaye666.websocket.model.vo.ResponseJson;

import javax.servlet.http.HttpSession;

/**
 * 登陆、登出
 */
public interface SecurityService {

    ResponseJson login(String username, String password, HttpSession session);
    
    ResponseJson logout(HttpSession session);
}
