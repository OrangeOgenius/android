package com.orange.tpms.helper;

import android.os.Handler;

/**
 * 类作用描述
 * Created by haide.yin() on 2019/6/6 9:27.
 */
public class HandlerHelper extends BaseHelper {

    private Handler handler;//Handler
    private Runnable runnable;//Runnable

    public HandlerHelper(){
        handler = new Handler();
        runnable = this::timeNext;
    }

    /**
     * 延迟触发
     *
     * @param milSecond the mil second
     */
    public void postDelay(int milSecond){
        handler.postDelayed(runnable,milSecond);
    }

    /**
     * 清除定时器
     */
    public void removeHandler(){
        handler.removeCallbacks(runnable);
        handler.removeMessages(0);
    }

    /* *********************************  时间到了  ************************************** */

    private OnTimeListener onTimeListener;

    public void timeNext(){
        if(onTimeListener != null){
            onTimeListener.onTime();
        }
    }

    public void setOnTimeListener(OnTimeListener onTimeListener){
        this.onTimeListener = onTimeListener;
    }

    public interface OnTimeListener{
        void onTime();
    }
}
