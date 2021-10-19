package top.chendaye666.websocket.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MD5UtilTest {

    @Test
    void MD5EncodeUtf8() {
        String chendaye = MD5Util.MD5EncodeUtf8("chendaye");
        System.out.println(chendaye);
    }
}