package top.chendaye666.client.client.pool;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * 建立channel、消息序列号和callback程序的对应关系
 */
public class ChannelUtils {
    public static final int MESSAGE_LENGTH = 16;
    // channel 的属性的 key： AttributeKey
    // 属性 DATA_MAP_ATTRIBUTEKEY 是一个 Map<Integer, Object>
    public static final AttributeKey<Map<Integer, Object>> DATA_MAP_ATTRIBUTEKEY = AttributeKey.valueOf("dataMap");

    /**
     * 设置属性值
     * @param channel
     * @param seq
     * @param callback
     * @param <T>
     */
    public static <T> void putCallback2DataMap(Channel channel, int seq, T callback) {
        // <消息序列号,回调函数>
        channel.attr(DATA_MAP_ATTRIBUTEKEY).get().put(seq, callback);
    }

    /**
     * 删除属性中的某个对应关系
     * @param channel
     * @param seq
     * @param <T>
     * @return
     */
    public static <T> T removeCallback(Channel channel, int seq) {
        return (T) channel.attr(DATA_MAP_ATTRIBUTEKEY).get().remove(seq);
    }
}