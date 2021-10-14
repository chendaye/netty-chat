package top.chendaye666.websocket.service;


import top.chendaye666.websocket.common.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * 登陆、登出
 */
public interface SecurityService {

    ServerResponse login(String username, String password, HttpSession session);

    ServerResponse logout(HttpSession session);
}
