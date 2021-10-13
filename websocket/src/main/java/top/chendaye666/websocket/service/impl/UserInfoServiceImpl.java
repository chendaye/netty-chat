package top.chendaye666.websocket.service.impl;

import top.chendaye666.websocket.dao.mock.UserInfoDao;
import top.chendaye666.websocket.model.po.UserInfo;
import top.chendaye666.websocket.model.vo.ResponseJson;
import top.chendaye666.websocket.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
