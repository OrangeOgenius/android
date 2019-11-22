package com.orange.tpms.lib.db.share;

import android.content.Context;
import com.orange.tpms.lib.utils.SharedPreferencesUtils;

/**
 * 服务器cookie
 * Created by john on 2019/4/27.
 */
public class ServerShare {

    public static final String UserKey = "userKey";
    public static final String PasswdKey = "passwdKey";
    public static final String TokenKey = "tokenKey";
    public static final String DeviceSN = "deviceSN";

    private static ServerShare instance = null;

    protected ServerShare () {
    }

    public static ServerShare getInstance () {
        if (instance == null) {
            instance = new ServerShare();
        }
        return instance;
    }

    public void setUser (Context context, String value) {
        SharedPreferencesUtils.setParam(context, UserKey, value);
    }

    public String getUser (Context context) {
        return (String)SharedPreferencesUtils.getParam(context, UserKey, "English");
    }

    public void setPasswd (Context context, String value) {
        SharedPreferencesUtils.setParam(context, PasswdKey, value);
    }

    public String getPasswd (Context context) {
        return (String)SharedPreferencesUtils.getParam(context, PasswdKey, "");
    }

    public void setDeviceSN (Context context, String deviceSN) {
        SharedPreferencesUtils.setParam(context, DeviceSN, deviceSN);
    }

    public String getDeviceSN (Context context) {
        return (String)SharedPreferencesUtils.getParam(context, DeviceSN, "");
    }

    public void setToken (Context context, String value) {
        SharedPreferencesUtils.setParam(context, TokenKey, value);
    }

    public String getToken (Context context) {
        return (String)SharedPreferencesUtils.getParam(context, TokenKey, "");
    }
}
