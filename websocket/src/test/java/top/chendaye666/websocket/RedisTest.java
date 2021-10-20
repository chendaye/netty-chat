package top.chendaye666.websocket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import top.chendaye666.websocket.model.po.UserToken;
import top.chendaye666.websocket.service.JWTService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    ValueOperations<String, String> valueOperations;

    @Autowired
    ValueOperations<String, Object> objectOperations;

    @Autowired
    ListOperations<Object, Object> listOperations;

    @Autowired
    RedisTemplate<String, Object> stringObjectRedisTemplate;

    @Autowired
    JWTService jwtService;

    @Test
    public void ValueOperationsTest(){
        valueOperations.set("chendaye666", "777");
    }

    @Test
    public void  ValueObjectOperationsTest(){
        UserToken userToken = new UserToken("123","token", 4, LocalDateTime.now(), LocalDateTime.now().plusSeconds(TimeUnit.MINUTES.toSeconds(120)), true);
        objectOperations.set("objectOperations", userToken);
        UserToken token = (UserToken)this.objectOperations.get("objectOperations");
        System.out.println(token);
    }

    @Test
    public void DeleteTokenTest(){
        String redisTokenKey = "token#8";
        Boolean delete = stringObjectRedisTemplate.delete(redisTokenKey);
        System.out.println(delete);
    }

    @Test
    public void stringObjectRedisTemplateTest(){
        Boolean operations = stringObjectRedisTemplate.expire("objectOperations", TimeUnit.MINUTES.toSeconds(120), TimeUnit.SECONDS);
        System.out.println(operations);
    }



    @Test
    public void ListOperationsTest(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("777");
        strings.add("888");
        listOperations.leftPushAll("lengo", strings);
    }
}
