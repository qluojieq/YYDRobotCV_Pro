package com.yongyida.yydrobotcv.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon on 2018/4/18
 **/
public class CommonUtils {


    private static String regPhonNum = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";//电话号码

//    private String regNameRule = "^[(a-zA-Z0-9\\u4e00-\\u9fa5)]{1,6}$";//取名规则（汉字、数字、字母）
//    private static String regPhonNum = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    private static String regName = "^[(a-zA-Z0-9\\u4e00-\\u9fa5)]{1,12}$";
    public static boolean isMatchPhone(String data){
        Pattern p = Pattern.compile(regPhonNum);
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


    private static Toast toast;
    public static Handler handler = new Handler(Looper.getMainLooper());

    public static void serviceToast(final Context mContext, final String str) {
        handler.post(new Runnable() {
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(mContext, str, Toast.LENGTH_LONG);
                    //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //设置显示时长
                    toast.setText(str);
                    //显示
                } else {
                    toast.setText(str);
                }
                toast.show();

            }
        });
    }
}
