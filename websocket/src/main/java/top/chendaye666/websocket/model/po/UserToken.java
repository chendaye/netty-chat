package top.chendaye666.websocket.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * https://www.cnblogs.com/sxdcgaq8080/p/9023644.html
 * token 类（存redis中）
 */
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1283786048011980426L;
    // token id
    private String id;
    // userId
    private Integer userId;
    // 授权时间
    private LocalDateTime loginAt;
    // 过期时间
    private LocalDateTime expiresAt;
    // 是否记住我
    private boolean remember;

    public UserToken() {
    }

    public UserToken(String id, Integer userId, LocalDateTime loginAt, LocalDateTime expiresAt, boolean remember) {
        this.id = id;
        this.userId = userId;
        this.loginAt = loginAt;
        this.expiresAt = expiresAt;
        this.remember = remember;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(LocalDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean getRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", loginAt=" + loginAt +
                ", expiresAt=" + expiresAt +
                ", remember=" + remember +
                '}';
    }
}
