package top.chendaye666.websocketold.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.chendaye666.websocketold.dao.UserInfoDao;
import top.chendaye666.websocketold.model.po.UserInfo;
import top.chendaye666.websocketold.model.vo.ResponseJson;
import top.chendaye666.websocketold.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;
    
    @Override
    public ResponseJson getByUserId(String userId) {
        UserInfo userInfo = userInfoDao.getByUserId(userId);
        return new ResponseJson().success()
                .setData("userInfo", userInfo);
    }

}
