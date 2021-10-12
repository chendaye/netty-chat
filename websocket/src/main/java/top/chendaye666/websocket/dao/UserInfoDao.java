package top.chendaye666.websocket.dao;

import top.chendaye666.websocket.model.po.UserInfo;

public interface UserInfoDao {

    void loadUserInfo();
    
    UserInfo getByUsername(String username);
    
    UserInfo getByUserId(String userId);
}
