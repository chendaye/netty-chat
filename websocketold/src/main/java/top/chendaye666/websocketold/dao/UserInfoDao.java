package top.chendaye666.websocketold.dao;

import top.chendaye666.websocketold.model.po.UserInfo;

public interface UserInfoDao {

    void loadUserInfo();
    
    UserInfo getByUsername(String username);
    
    UserInfo getByUserId(String userId);
}
