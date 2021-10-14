package top.chendaye666.websocket.service.impl;

import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.mock.UserInfoDao;
import top.chendaye666.websocket.model.po.UserInfo;
import top.chendaye666.websocket.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;
    
    @Override
    public ServerResponse getByUserId(String userId) {
        UserInfo userInfo = userInfoDao.getByUserId(userId);
        HashMap<String, UserInfo> data = new HashMap<>();
        return ServerResponse.createBySuccess(data);
    }

}
