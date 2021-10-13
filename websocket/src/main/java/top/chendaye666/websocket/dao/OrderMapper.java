package top.chendaye666.websocket.dao;

import org.apache.ibatis.annotations.Param;
import top.chendaye666.websocket.model.po.User;

/**
 * 一对一 关联
 * 方法一：核心思想扩展Order对象，来完成映射
 */
public interface OrderMapper {
    User queryOrderUserByOrderNumber(@Param("number") String number);
}
