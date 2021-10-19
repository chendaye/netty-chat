package top.chendaye666.websocket.service;

import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.model.po.User;

import java.util.HashMap;


public interface IUserService {
    ServerResponse<HashMap<String, Object>> login(String username, String password, Boolean remember);

    ServerResponse<String> logout(Integer userId);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(Integer userId, String oldPassword, String newPassword);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);
}
