package top.chendaye666.websocket.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import top.chendaye666.websocket.Exception.BizException;
import top.chendaye666.websocket.model.po.User;
import org.springframework.beans.factory.annotation.Value;
import top.chendaye666.websocket.model.po.UserToken;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * https://cloud.tencent.com/developer/article/1535973
 */
@Slf4j
@Service
public class JWTService {
    private static final String EXP = "exp";

    private static final String PAYLOAD = "payload";

    @Value("${jwt.key}")
    private String jwtKey;		// 从配置中读取到 jwt 的key

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    RedisTemplate<String, Object> stringObjectRedisTemplate;


    /**
     * 登陆成功，创建token
     * @param user
     * @param remember
     * @return
     */
    public  String createRedisToken(User user, Boolean remember){
        // 登陆时间
        LocalDateTime loginAt = LocalDateTime.now();
        // 过期时间，如果是“记住我”，则Token有效期是7天，反之则是2个小时
        LocalDateTime expireAt = loginAt.plusSeconds(remember ? TimeUnit.DAYS.toSeconds(7) : TimeUnit.MINUTES.toSeconds(120));
        // 距离过期时间剩余的秒数
        int expiresSeconds = (int) Duration.between(loginAt, expireAt).getSeconds();

        // 存储token对象到Redis中
        UserToken userToken = new UserToken();
        userToken.setId(UUID.randomUUID().toString().replace("-", "#"));
        userToken.setUserId(user.getId());
        userToken.setLoginAt(loginAt);
        userToken.setExpiresAt(expireAt);
        userToken.setRemember(remember);
        valueOperations.set("token#"+user.getId(), userToken, expiresSeconds, TimeUnit.SECONDS);
        return  createToken(user, userToken);
    }

    /**
     * 加密生成token
     */
    public  String createToken(User user, UserToken userToken) {
        try {
            final Algorithm signer = Algorithm.HMAC256(jwtKey);//生成签名
            Map<String, Object> jwtHeader = new HashMap<>();
            jwtHeader.put("alg", "alg");
            jwtHeader.put("JWT", "JWT");
            String token = JWT.create()
                    .withHeader(jwtHeader)
                    .withIssuer("签发者")
                    .withSubject("用户")//主题，科目
                    .withClaim("username", user.getUsername())
                    .withClaim("userId", user.getId())
                    .withClaim("password",user.getPassword())
                    // 这里不在Token上设置过期时间，过期时间由Redis维护
                    // .withExpiresAt(new Date(System.currentTimeMillis() + maxAge))
                    .withJWTId(userToken.getId())
                    .sign(signer);
            return Base64.getEncoder().encodeToString(token.getBytes("utf-8"));
        } catch (Exception e) {
            log.error("生成token异常：", e);
            return null;
        }
    }

    /**
     * 删除 key
     * @param user
     * @return
     */
    public  Boolean deleteToken(User user) {
        String redisTokenKey = "token#"+user.getId();
        Boolean delete = stringObjectRedisTemplate.delete(redisTokenKey);
        return delete;
    }


    /**
     * 解析验证token
     *
     * @param token  加密后的token字符串
     * @return
     */
    public  DecodedJWT verifyToken(String token) {
        try {
            // 解析请求中的token
            Algorithm algorithm = Algorithm.HMAC256(jwtKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(new String(Base64.getDecoder().decode(token),"utf-8"));
            // token 中保存的 userId
            Integer userId = jwt.getClaim("userId").asInt();
            // 判断请求中的token 和 redis 中的 token 是否一致
            if (userId != null){
                // 从redis 中取token
                String redisTokenKey = "token#"+userId;
                UserToken userToken = JSON.parseObject(valueOperations.get(redisTokenKey).toString(), UserToken.class);
                if (userToken != null && userToken.getId().equals(jwt.getId()) && userToken.getUserId().equals(userId)){
                    System.out.println(">>>>>UserToken="+userToken.toString());
                    //token 合法，续约
                    long renewTime = userToken.getRemember() ? TimeUnit.DAYS.toSeconds(7) : TimeUnit.MINUTES.toSeconds(120);
                    Boolean status = stringObjectRedisTemplate.expire(redisTokenKey, renewTime, TimeUnit.SECONDS);
                }else{
                    throw new BizException("9999","token 无效！");
                }
            }else {
                throw new BizException("9999","token 不存在！");
            }
            return jwt;
        } catch (IllegalArgumentException | JWTVerificationException | UnsupportedEncodingException e) {
            throw new BizException("9999",e.getMessage());
        }
    }
}
