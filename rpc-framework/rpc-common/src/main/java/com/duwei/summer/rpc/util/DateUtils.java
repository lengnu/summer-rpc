package com.duwei.summer.rpc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *  时间工具类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 20:28
 * @since: 1.0
 */
public class DateUtils {
    public static Date parse(String pattern){
        SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
