package top.chendaye666.websocket.service.impl;

import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.mock.UserInfoDao;
import top.chendaye666.websocket.model.po.UserInfo;
import top.chendaye666.websocket.service.SecurityService;
import top.chendaye666.websocket.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    private UserInfoDao userInfoDao;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    
    
    @Override
    public ServerResponse login(String username, String password, HttpSession session) {
        UserInfo userInfo = userInfoDao.getByUsername(username);
        if (userInfo == null) {
            return ServerResponse.createByErrorMessage("不存在该用户名");
        }
        if (!userInfo.getPassword().equals(password)) {
            return ServerResponse.createByErrorMessage("密码不正确");
        }
        session.setAttribute(Constant.USER_TOKEN, userInfo.getUserId());
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse logout(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        if (userId == null) {
            return ServerResponse.createByErrorMessage("请先登录！");
        }
        session.removeAttribute(Constant.USER_TOKEN);
        LOGGER.info(MessageFormat.format("userId为 {0} 的用户已注销登录!", userId));
        return ServerResponse.createBySuccess();
    }

}
