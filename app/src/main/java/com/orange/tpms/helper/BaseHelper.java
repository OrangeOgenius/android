package com.orange.tpms.helper;

import android.os.Handler;
import android.os.Looper;

/**
 * Helper基类
 * Created by haide.yin() on 2019/4/4 14:28.
 */
public class BaseHelper {

    private Handler mainHandler;//主线程

    public BaseHelper(){
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 判断当前线程是否为主线程
     * @return 是否是主线程
     */
    private boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 在主线程运行
     */
    public void runMainThread(Runnable runnable){
        mainHandler.post(runnable);
    }

    /* *********************************  开始请求  ************************************** */

    private OnPreRequestListener onPreRequestListener;

    public void preRequestNext(){
        if(onPreRequestListener != null){
            runMainThread(() -> onPreRequestListener.onPreRequest());
        }
    }

    public void setOnPreRequestListener(OnPreRequestListener onPreRequestListener){
        this.onPreRequestListener = onPreRequestListener;
    }

    public interface OnPreRequestListener{
        void onPreRequest();
    }

    /* *********************************  请求成功  ************************************** */

    private OnSuccessRequestListener onSuccessRequestListener;

    public void successRequestNext(Object object){
        if(onSuccessRequestListener != null){
            runMainThread(() -> onSuccessRequestListener.onSuccessRequest(object));
        }
    }

    public void setOnSuccessRequestListener(OnSuccessRequestListener onSuccessRequestListener){
        this.onSuccessRequestListener = onSuccessRequestListener;
    }

    public interface OnSuccessRequestListener{
        void onSuccessRequest(Object object);
    }

    /* *********************************  请求失败  ************************************** */

    private OnFailedRequestListener onFailedRequestListener;

    public void failedRequestNext(Object object){
        if(onFailedRequestListener != null){
            runMainThread(() -> onFailedRequestListener.onFailedRequest(object));
        }
    }

    public void setOnFailedRequestListener(OnFailedRequestListener onFailedRequestListener){
        this.onFailedRequestListener = onFailedRequestListener;
    }

    public interface OnFailedRequestListener{
        void onFailedRequest(Object object);
    }

    /* *********************************  结束请求  ************************************** */

    private OnFinishRequestListener onFinishRequestListener;

    public void finishRequestNext(){
        if(onFinishRequestListener != null){
            runMainThread(() -> onFinishRequestListener.onFinishRequest());
        }
    }

    public void setOnFinishRequestListener(OnFinishRequestListener onFinishRequestListener){
        this.onFinishRequestListener = onFinishRequestListener;
    }

    public interface OnFinishRequestListener{
        void onFinishRequest();
    }
}
