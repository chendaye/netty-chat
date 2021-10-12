package top.chendaye666.websocketold.service;

import javax.servlet.http.HttpSession;

import top.chendaye666.websocketold.model.vo.ResponseJson;

public interface SecurityService {

    ResponseJson login(String username, String password, HttpSession session);
    
    ResponseJson logout(HttpSession session);
}
