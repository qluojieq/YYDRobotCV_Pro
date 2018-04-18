package com.yongyida.yydrobotcv.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon on 2018/4/18
 **/
public class CommonUtils {
    private static String regPhonNum = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    private static String regName = "^[(a-zA-Z0-9\\u4e00-\\u9fa5)]{1,8}$";
    public static boolean isMatch(String data,String reg){
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(data);
        boolean isMatch = m.matches();
        return  isMatch;
    }
    public static boolean isMatchName(String data){
        Pattern p = Pattern.compile(regName);
        Matcher m = p.matcher(data);
        boolean isMatch = m.matches();
        return  isMatch;
    }
}
