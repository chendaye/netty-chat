package top.chendaye666.websocket.dao;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.impl.UserServiceImpl;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * https://blog.csdn.net/weixin_39800144/article/details/79241620
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@WebAppConfiguration
class UserMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    void deleteByPrimaryKey() {
    }

    @Test
    void insert() {
        User user = new User(4,
                "chendaye",
                "wewewrwerwe",
                "chendaye666@gmail.com",
                "15271834241",
                "https://blog.csdn.net/u012643122/article/details/52890772",
                2,
                "2021-10-14 15:22:22",
                "2021-10-14 15:22:22"
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