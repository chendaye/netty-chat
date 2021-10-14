package top.chendaye666.websocket.Exception;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * @description: 异常处理枚举类
 */
public enum ExceptionEnum implements BaseErrorInfoInterface{

    // 数据操作错误定义
    SUCCESS("2000", "成功!"),
    BODY_NOT_MATCH("4000","请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("4001","请求的数字签名不匹配!"),
    NOT_FOUND("4004", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("5000", "服务器内部错误!"),
    SERVER_BUSY("5003","服务器正忙，请稍后再试!"),
    ERROR("1", "ERROR"),
    NEED_LOGIN("10", "NEED_LOGIN"),
    ILLEGAL_ARGUMENT("2", "ILLEGAL_ARGUMENT");


    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    ExceptionEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }

    @Override
    public String toString() {
        HashMap<String, String> data = new HashMap<>();
        data.put(this.resultCode, this.resultMsg);
        return JSONObject.toJSONString(data);
    }
}

