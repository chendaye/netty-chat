package top.chendaye666.websocket.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.impl.UserServiceImpl;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    void deleteByPrimaryKey() {
    }

    @Test
    void insert() {
        User user = new User(1,
                "chendaye",
                "wewewrwerwe",
                "chendaye666@gmail.com",
                "15271834241",
                "https://blog.csdn.net/u012643122/article/details/52890772",
                1,
                new Date(),
                new Date()
        );
        int insert = userMapper.insert(user);
        System.out.println(insert);
//        UserServiceImpl userService = new UserServiceImpl();
//        ServerResponse<String> register = userService.register(user);
//        System.out.println(register.getData());
    }

    @Test
    void insertSelective() {
    }

    @Test
    void selectByPrimaryKey() {
    }

    @Test
    void updateByPrimaryKeySelective() {
    }

    @Test
    void updateByPrimaryKey() {
    }

    @Test
    void checkUsername() {
    }

    @Test
    void checkEmail() {
    }

    @Test
    void selectLogin() {
    }

    @Test
    void updatePasswordByUsername() {
    }

    @Test
    void checkPassword() {
    }

    @Test
    void checkEmailByUserId() {
    }
}