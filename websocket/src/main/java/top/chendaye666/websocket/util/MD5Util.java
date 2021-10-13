package top.chendaye666.websocket.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /**
     * 字节数组转化为16进制
     * @param b
     * @return
     */
    private  static String byteArrayToHexString(byte b[]){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++){
            builder.append(byteToHexString(b[i]));
        }
        return builder.toString();
    }

    private static String byteToHexString(byte b){
        int n = b;
        if (n < 0) n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 返回大写的MD5
     * @param origin
     * @param charsetname
     * @return
     */
    private static String MD5Encode(String origin, String charsetname){
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)){
                resultString = byteArrayToHexString(md5.digest(resultString.getBytes()));
            }else {
                resultString = byteArrayToHexString(md5.digest(resultString.getBytes(charsetname)));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultString.toUpperCase();
    }

    public static String MD5EncodeUtf8(String origin){
        origin = origin + PropertiesUtil.getProperty("password.salt", "");
        return MD5Encode(origin, "utf-8");
    }
}
