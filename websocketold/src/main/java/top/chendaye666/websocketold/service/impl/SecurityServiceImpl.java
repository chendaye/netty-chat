package top.chendaye666.websocketold.service.impl;

import java.text.MessageFormat;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.chendaye666.websocketold.dao.UserInfoDao;
import top.chendaye666.websocketold.model.po.UserInfo;
import top.chendaye666.websocketold.model.vo.ResponseJson;
import top.chendaye666.websocketold.service.SecurityService;
import top.chendaye666.websocketold.util.Constant;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    private UserInfoDao userInfoDao;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    
    
    @Override
    public ResponseJson login(String username, String password, HttpSession session) {
        UserInfo userInfo = userInfoDao.getByUsername(username);
        if (userInfo == null) {
            return new ResponseJson().error("不存在该用户名");
        }
        if (!userInfo.getPassword().equals(password)) {
            return new ResponseJson().error("密码不正确");
        }
        session.setAttribute(Constant.USER_TOKEN, userInfo.getUserId());
        return new ResponseJson().success();
    }

    @Override
    public ResponseJson logout(HttpSession session) {
        Object userId = session.getAttribute(Constant.USER_TOKEN);
        if (userId == null) {
            return new ResponseJson().error("请先登录！");
        }
        session.removeAttribute(Constant.USER_TOKEN);
        LOGGER.info(MessageFormat.format("userId为 {0} 的用户已注销登录!", userId));
        return new ResponseJson().success();
    }

}
