package top.chendaye666.websocket.service;

import top.chendaye666.websocket.model.vo.ResponseJson;

/**
 * 用户信息
 */
public interface UserInfoService {

    ResponseJson getByUserId(String userId);
}
