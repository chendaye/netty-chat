package top.chendaye666.websocket.web.controller;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.IUserService;


import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private IUserService iUserService;

    @Autowired
    private MockMvc mockMvc;

    Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Before
    public void init() {
//        mockMvc = MockMvcBuilders.standaloneSetup(UserController.class).build();
        logger.info("开始测试...");
    }

    @After
    public void after() {
        logger.info("测试结束...");
    }


    @Test
    void register() throws UnsupportedEncodingException, Exception {
        User user = new User();
        user.setUsername("chendaye777");
        user.setPassword("chendaye777");
        user.setAvatar("777777");
        user.setEmail("77777");
        user.setPhone("77777");
        user.setRole(1);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(user)))
                .andDo(MockMvcResultHandlers.print()
                )
                .andReturn();

        logger.info(mvcResult.getResponse().getContentAsString());
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
    void userInfo() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "1");
//        Mockito.when(iUserService.checkValid()).thenReturn("假数据");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/userinfo").params(params))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void forgetPasswd() {
    }

    @Test
    void resetPasswd() {
    }

    @Test
    void updateInfo() throws Exception {

    }
}