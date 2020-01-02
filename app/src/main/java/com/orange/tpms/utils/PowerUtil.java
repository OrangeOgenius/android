package com.orange.tpms.utils;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

//注意，PowerManager只有系统应用才能操作，普通应用不能操作，所以下面代码仅供参考
public class PowerUtil {

    private final static String TAG = "PowerUtil";

    private static int getValue(Context ctx, String methodName, int defValue) {
        int value = defValue;
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        try {
            Class<?> pmClass = Class.forName(pm.getClass().getName());
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            Object iPM = field.get(pm);
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            Method method = iPMClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
            value = (Integer) method.invoke(iPM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "methodName="+methodName+", value="+value);
        return value;
    }

    public static int getMinLight(Context ctx) {
        return getValue(ctx, "getMinimumScreenBrightnessSetting", 0);
    }

    public static int getMaxLight(Context ctx) {
        return getValue(ctx, "getMaximumScreenBrightnessSetting", 255);
    }

    public static int getDefLight(Context ctx) {
        return getValue(ctx, "getDefaultScreenBrightnessSetting", 100);
    }

    //设置屏幕亮度。light取值0-255
    public static void setLight(Context ctx, int light) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        try {
            Class<?> pmClass = Class.forName(pm.getClass().getName());
            // 得到PowerManager类中的成员mService（mService为PowerManagerService类型）
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            // 实例化mService
            Object iPM = field.get(pm);
            // 得到PowerManagerService对应的Class对象
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            /*
             * 得到PowerManagerService的函数setBacklightBrightness对应的Method对象，
             * PowerManager的函数setBacklightBrightness实现在PowerManagerService中
             */
            Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);
            method.setAccessible(true);
            // 调用实现PowerManagerService的setBacklightBrightness
            method.invoke(iPM, light);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetLight(Context ctx, int light) {
        try {
            Object power;
            Class <?> ServiceManager = Class.forName("android.os.ServiceManager");
            Class <?> Stub = Class.forName("android.os.IPowerManager$Stub");

            Method getService = ServiceManager.getMethod("getService", new Class[] {String.class});
            //Method asInterface = GetStub.getMethod("asInterface", new Class[] {IBinder.class});//of this class?
            Method asInterface = Stub.getMethod("asInterface", new Class[] {IBinder.class});    //of this class?
            IBinder iBinder = (IBinder) getService.invoke(null, new Object[] {Context.POWER_SERVICE});//
            power = asInterface.invoke(null,iBinder);//or call constructor Stub?//

            Method setBacklightBrightness = power.getClass().getMethod("setBacklightBrightness", new Class[]{int.class});
            setBacklightBrightness.invoke(power, new Object[]{light});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //锁屏
//    public static void lockScreen(Context ctx) {
//        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
//        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
//        wl.acquire();
//        wl.release();
//    }
//
//    //解锁
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    public static void unLockScreen(Context ctx) {
//
//    }

    //重启
    public static void reboot(Context ctx) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }

    //关机
    public static void shutDown(Context ctx) {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 弹出系统内置的对话框，选择确定关机或取消关机
        ctx.startActivity(intent);
    }

}