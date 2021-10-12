package top.chendaye666.websocket.service;

import top.chendaye666.websocket.model.vo.ResponseJson;

public interface UserInfoService {

    ResponseJson getByUserId(String userId);
}
