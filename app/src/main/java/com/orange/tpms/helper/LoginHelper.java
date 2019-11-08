package com.orange.tpms.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.de.rocket.utils.SharePreUtil;
import com.orange.tpms.lib.api.Server;
import bean.server.req.LoginBeanReq;

public class LoginHelper extends BaseHelper {

    //此处设置默认文件名
    public static final String LOGIN_SP_NAME = "login_sp";
    //是否登陆的Key
    public static final String HAS_LOGIN = "login_key";
    //是否完成登陆配置的Key
    public static final String HAS_CONFIG = "config_key";
    //用户名
    public static final String User_ACCOUNT = "account_key";
    //用户名
    public static final String User_PASSWORD = "password_key";

    /**
     * 获取车的Makes
     * @param context 上下文
     * @param username 用户名
     * @param password 密码
     */
    public void login(Context context, String username, String password) {
       /* preRequestNext();
        LoginBeanReq loginBeanReq = new LoginBeanReq();
        loginBeanReq.setUserId(username);
        loginBeanReq.setPasswd(password);
        Server server = new Server(context);
        server.register(loginBeanReq, (respond) -> {
            int status = respond.getStatus();
            String message = respond.getMessage();
            String deviceToken = respond.getData().getDeviceToken();
            if (status == 200) {
                //请求成功
                setAccount(context,username);
                setPassword(context,password);
                LoginHelper.setHasLogin(context,true);
                loginSuccessNext();
            } else {
                //请求失败
                onLoginFailedNext(message);
            }
            finishRequestNext();
        });*/
        new Thread(() -> {
            //请求成功
            preRequestNext();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setAccount(context,username);
            setPassword(context,password);
            LoginHelper.setHasLogin(context,true);
            loginSuccessNext();
        }).start();
    }

    /**
     * 退出登陆
     * @param context 上下文
     */
    public void logout(Context context){
        setHasLogin(context,false);
        logoutSuccessNext();
    }

    /**
     * 设置登陆状态
     * @param context 上下文
     * @param hasLogin 是否登陆
     */
    public static void setHasLogin(Context context, boolean hasLogin) {
        SharePreUtil.getInstance().putBoolean(context,HAS_LOGIN,hasLogin);
    }

    /**
     * 获取登陆状态不
     * @param context 上下文
     * @return  hasLogin 是否登陆
     */
    public static boolean getHasLogin(Context context) {
        return SharePreUtil.getInstance().getBoolean(context,HAS_LOGIN,false);
    }

    /**
     * 设置是否完成登陆配置
     * @param context 上下文
     * @param hasConfig 是否完成登陆配置
     */
    public static void setHasConfig(Context context, boolean hasConfig) {
        SharePreUtil.getInstance().putBoolean(context,HAS_CONFIG,hasConfig);
    }

    /**
     * 获取是否完成登陆配置
     * @param context 上下文
     * @return  hasLogin 是否完成登陆配置
     */
    public static boolean getHasConfig(Context context) {
        return SharePreUtil.getInstance().getBoolean(context,HAS_CONFIG,false);
    }

    /**
     * 设置用户名
     * @param context 上下文
     * @param account 用户名
     */
    private static void setAccount(Context context, String account) {
        SharePreUtil.getInstance().putString(context,User_ACCOUNT,account);
    }

    /**
     * 获取用户名
     * @param context 上下文
     * @return  用户名
     */
    public static String getAccount(Context context) {
        return SharePreUtil.getInstance().getString(context,User_ACCOUNT,"admin@example.com");
    }

    /**
     * 设置用户密码
     * @param context 上下文
     * @param password 用户密码
     */
    private static void setPassword(Context context, String password) {
        SharePreUtil.getInstance().putString(context,User_PASSWORD,password);
    }

    /**
     * 获取用户密码
     * @param context 上下文
     * @return  用户密码
     */
    public static String getPassword(Context context) {
        return SharePreUtil.getInstance().getString(context,User_PASSWORD,"DeviceSN04");
    }

    /* *********************************  登陆成功  ************************************** */

    private OnLoginSuccessListener onLoginSuccessListener;

    public void loginSuccessNext() {
        if (onLoginSuccessListener != null) {
            runMainThread(() -> onLoginSuccessListener.onLoginSuccess());
        }
    }

    public void setOnLoginSuccessListener(OnLoginSuccessListener onLoginSuccessListener) {
        this.onLoginSuccessListener = onLoginSuccessListener;
    }

    public interface OnLoginSuccessListener {
        void onLoginSuccess();
    }

    /* ***************************** LoginFailed ***************************** */

    private OnLoginFailedListener onLoginFailedListener;

    // 接口类 -> OnLoginFailedListener
    public interface OnLoginFailedListener {
        void onLoginFailed(String message);
    }

    // 对外暴露接口 -> setOnLoginFailedListener
    public void setOnLoginFailedListener(OnLoginFailedListener onLoginFailedListener) {
        this.onLoginFailedListener = onLoginFailedListener;
    }

    // 内部使用方法 -> LoginFailedNext
    private void onLoginFailedNext(String message) {
        if (onLoginFailedListener != null) {
            runMainThread(() -> onLoginFailedListener.onLoginFailed(message));
        }
    }

    /* *********************************  退出登陆成功  ************************************** */

    private OnLogoutSuccessListener onLogoutSuccessListener;

    public void logoutSuccessNext() {
        if (onLogoutSuccessListener != null) {
            runMainThread(() -> onLogoutSuccessListener.onLogoutSuccess());
        }
    }

    public void setOnLogoutSuccessListener(OnLogoutSuccessListener onLogoutSuccessListener) {
        this.onLogoutSuccessListener = onLogoutSuccessListener;
    }

    public interface OnLogoutSuccessListener {
        void onLogoutSuccess();
    }
}
