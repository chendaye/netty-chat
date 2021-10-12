package top.chendaye666.websocket.util;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import com.auth0.jwt.JWT;
import top.chendaye666.websocket.Exception.BizException;
import top.chendaye666.websocket.model.po.UserInfo;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

/**
 * https://cloud.tencent.com/developer/article/1535973
 */
@Slf4j
public class JWTUtil {
    private static final String EXP = "exp";

    private static final String PAYLOAD = "payload";


    /**
     * 加密生成token
     *
     * @param object 载体信息
     * @param maxAge 有效时长
     * @param secret 服务器私钥
     * @param <T>
     * @return
     */
    public static String createToken(UserInfo object, long maxAge, String secret) {
        try {
            final Algorithm signer = Algorithm.HMAC256(secret);//生成签名
            String token = JWT.create()
                    .withIssuer("签发者")
                    .withSubject("用户")//主题，科目
                    .withClaim("username", object.getUsername())
                    .withClaim("id", object.getUserId())
                    .withClaim("password",object.getPassword())
                    .withExpiresAt(new Date(System.currentTimeMillis() + maxAge))
                    .sign(signer);
            System.out.println(token);
            return Base64.getEncoder().encodeToString(token.getBytes("utf-8"));
        } catch (Exception e) {
            log.error("生成token异常：", e);
            return null;
        }
    }

    /**
     * 解析验证token
     *
     * @param token  加密后的token字符串
     * @param secret 服务器私钥
     * @return
     */
    public static DecodedJWT verifyToken(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(new String(Base64.getDecoder().decode(token),"utf-8"));
            return jwt;
        } catch (IllegalArgumentException e) {
            throw new BizException("9999",e.getMessage());
        } catch (JWTVerificationException e) {
            throw new BizException("9999",e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new BizException("9999",e.getMessage());
        }
    }
}
