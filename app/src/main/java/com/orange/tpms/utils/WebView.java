package com.orange.tpms.utils;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WebView extends android.webkit.WebView {
    public WebView(Context context) {
        super(context);
    }

    public WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }


}
