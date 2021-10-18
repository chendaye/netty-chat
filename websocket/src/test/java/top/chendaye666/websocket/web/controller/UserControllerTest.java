package top.chendaye666.websocket.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@WebAppConfiguration
class UserControllerTest {

    @Test
    void register() {

    }

    @Test
    void checkValid() {
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void userInfo() {
    }

    @Test
    void forgetPasswd() {
    }

    @Test
    void resetPasswd() {
    }

    @Test
    void updateInfo() {
    }
}