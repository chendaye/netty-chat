package top.chendaye666.websocketold.service;

import top.chendaye666.websocketold.model.vo.ResponseJson;

public interface UserInfoService {

    ResponseJson getByUserId(String userId);
}
