package top.chendaye666.client.client.pool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取消息seq
 */
public class IntegerFactory {
    private static class SingletonHolder {
        // 原子计数器
        private static final AtomicInteger INSTANCE = new AtomicInteger();
    }

    private IntegerFactory(){}

    public static final AtomicInteger getInstance() {
        return SingletonHolder.INSTANCE;
    }
}