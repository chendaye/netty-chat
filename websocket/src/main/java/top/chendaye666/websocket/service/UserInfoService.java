package top.chendaye666.websocket.service;


import top.chendaye666.websocket.common.ServerResponse;

/**
 * 用户信息
 */
public interface UserInfoService {

    ServerResponse getByUserId(String userId);
}
