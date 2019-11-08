package com.orange.tpms.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * 键盘的工具类
 * Created by haide.yin() on 2019/9/30 14:42.
 */
public class KeyboardUtil {

    /**
     * 隐藏软键盘
     */
    public static void hideEditTextKeyboard(EditText editText){
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
            Log.v("yhd-","hideEditTextKeyboard:"+e.toString());
        }
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

    /**
     * 显示软键盘
     *
     * @param activity the activity
     */
    public static void showKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInputFromInputMethod(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 强制显示软键盘
     *
     * @param activity the activity
     */
    public static void forceShowKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
