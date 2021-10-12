package top.chendaye666.websocket.Exception;

/**
 * https://juejin.cn/post/6959520793063587848
 * @description: 服务接口类
 */
public interface BaseErrorInfoInterface {

    /**
     *  错误码
     * @return
     */
    String getResultCode();

    /**
     * 错误描述
     * @return
     */
    String getResultMsg();
}

