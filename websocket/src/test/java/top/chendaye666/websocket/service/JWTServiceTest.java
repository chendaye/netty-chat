package top.chendaye666.websocket.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class JWTServiceTest {
    @Autowired
    JWTService jwtService;

    @Test
    void createRedisToken() {
    }

    @Test
    void getRedisToken() {
    }

    @Test
    void getRedisKey() {
    }

    @Test
    void createToken() {
    }

    @Test
    void deleteToken() {
    }

    @Test
    void verifyToken() {
        String token = "ZXlKaGJHY2lPaUpJVXpJMU5pSXNJa3BYVkNJNklrcFhWQ0lzSW5SNWNDSTZJa3BYVkNKOS5leUp6ZFdJaU9pTG5sS2ptaUxjaUxDSndZWE56ZDI5eVpDSTZJaUlzSW1semN5STZJdWV0dnVXUGtlaUFoU0lzSW5WelpYSkpaQ0k2T0N3aWFuUnBJam9pWXpZMU9EZzBZbVFqWXpReU15TTBPREZpSTJFNVl6VWpNR1JoT0dSak9HWmxNVGcxSWl3aWRYTmxjbTVoYldVaU9pSnVaWFIwZVRBeEluMC54QmpuZmRpX2E4aXF3NVY1dFE5ZkJfNDlOZ2x4UFdfMjI5QkRkRldhd3pF";
        jwtService.verifyToken(token);
    }
}