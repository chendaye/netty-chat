package top.chendaye666.websocket.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class  DateUtil {
    private static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 当前时间
     * @return
     */
    public static String nowTime(){
        return  new SimpleDateFormat(defaultFormat).format(new Date());
    }

}
