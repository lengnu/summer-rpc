package com.duwei.summer.rpc.util;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 15:12
 * @since: 1.0
 */
public class StringUtils {
    public static boolean isBlank(String str){
        if (str == null || str.length() == 0){
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
