package com.orange.tpms.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * 数字类的工具集合
 * Created by haide.yin() on 2019/9/5 13:33.
 */
public class NumberUtil {

    /**
     * 是否是纯数字
     */
    public static boolean isNumber(String content){
        if(!TextUtils.isEmpty(content)){
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(content).matches();
        }
        return false;
    }

    /**
     * 浮点类型保留小数位
     */
    public static String toFormate(float content){
        return toFormate(content,"0.00");
    }

    /**
     * 浮点类型保留小数位
     */
    public static String toFormate(float content,String patten){
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat(patten);
        //format 返回的是字符串
        return decimalFormat.format(content);
    }
}
