package top.chendaye666.websocket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    ValueOperations<String, String> valueOperations;
    @Autowired
    ListOperations<Object, Object> listOperations;
    @Test
    public void ValueOperationsTest(){
        valueOperations.set("chendaye666", "777");
    }

    @Test
    public void ListOperationsTest(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("777");
        strings.add("888");
        listOperations.leftPushAll("lengo", strings);
    }
}
