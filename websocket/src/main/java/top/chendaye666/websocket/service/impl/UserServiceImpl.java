package top.chendaye666.websocket.service.impl;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chendaye666.websocket.common.Const;
import top.chendaye666.websocket.common.ServerResponse;
import top.chendaye666.websocket.dao.UserMapper;
import top.chendaye666.websocket.model.po.User;
import top.chendaye666.websocket.service.IUserService;
import top.chendaye666.websocket.util.DateUtil;
import top.chendaye666.websocket.service.JWTService;
import top.chendaye666.websocket.util.MD5Util;


import java.util.HashMap;

@Service("iUserService") // 注册为服务
public class UserServiceImpl implements IUserService {
    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private JWTService jwtService;

    /**
     * 检查username email是否存在
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type){
        if (StringUtils.isNotBlank(type)){
            // 验证用户名是否存在
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已经存在！");
                }
            }
            // 验证邮箱是否存在
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已经存在！");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验成功!");
    }


    /**
     * 用户注册
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user){
        /*检查姓名 邮箱重复*/
        ServerResponse vaildResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!vaildResponse.isSuccess()) return vaildResponse;
        vaildResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!vaildResponse.isSuccess()) return vaildResponse;

        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setCreateTime(DateUtil.nowTime());
        user.setUpdateTime(DateUtil.nowTime());
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败！");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    /**
     * 登出
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<String> logout(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        Boolean status = jwtService.deleteToken(user);
        return status ? ServerResponse.createBySuccess("登出成功！") : ServerResponse.createByErrorMessage("登出失败！");
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<HashMap<String, Object>> login(String username, String password, Boolean remember) {
        int result = userMapper.checkUsername(username);
        if (result == 0){
            return ServerResponse.createByErrorMessage("用户名不存在!");
        }
        //todo： MD5 密码登录
        String md5Passwd = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Passwd);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误！");
        }
        // 信息正确
        user.setPassword(StringUtils.EMPTY);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        // 获取token
        String redisToken = jwtService.createRedisToken(user, remember);
        map.put("token", redisToken);
        return ServerResponse.createBySuccess("登录成功！", map);
    }


    /**
     * 找回密码
     * @param username
     * @param passwordNew
     * @return
     */
    public ServerResponse<String> forgetResetPassword(String username, String email, String passwordNew){
        if(org.apache.commons.lang3.StringUtils.isBlank(email)){
            return ServerResponse.createByErrorMessage("参数错误,请输入正确的邮箱！");
        }
        // false 表示已经存在
        ServerResponse validUsername = this.checkValid(username,Const.USERNAME);
        if(validUsername.isSuccess() ){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        ServerResponse<String> validEmail = this.checkValid(email, Const.EMAIL);
        if (validEmail.isSuccess()){
            // 邮箱不存在
            return ServerResponse.createByErrorMessage("邮箱不存在");
        }
        String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
        int rowCount = userMapper.updatePasswordByUsername(username,md5Password,DateUtil.nowTime());
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public ServerResponse<String> resetPassword(Integer userId, String oldPassword, String newPassword){

        User user = userMapper.selectByPrimaryKey(userId);
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 更新信息
     * @param user
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAvatar(user.getAvatar());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
//        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
//            return ServerResponse.createBySuccess();
//        }
        return ServerResponse.createByError();
    }

}
