package com.orange.tpms.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 常用工具类
 */
public class OggUtils {

    /**
     * 判断空间内容是否为空
     * @param editText View
     * @return boolean empty
     */
    public static boolean isEmpty(EditText editText){
        if(editText != null){
            String content = editText.getText().toString().trim().replace(" ","");
            return content.isEmpty();
        }
        return false;
    }

    /**
     * 判断空间内容是否为空
     * @param textView View
     * @return boolean empty
     */
    public static boolean isEmpty(TextView textView){
        if(textView != null){
            String content = textView.getText().toString().trim().replace(" ","");
            return content.isEmpty();
        }
        return false;
    }

    /**
     * 获取输入框内容并去除所有空格
     *
     * @param ed 输入框
     * @return 字符 ed
     */
    public static String getEd(EditText ed) {
        return ed.getText().toString().trim().replace(" ", "");
    }

    /**
     * 获取输入框内容并去除所有空格
     *
     * @param tx 输入框
     * @return 字符 tx
     */
    public static String getTx(TextView tx) {
        return tx.getText().toString().trim();
    }

    /**
     * 隐藏软键盘
     *
     * @param activity the activity
     */
    public static void hideKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
