package com.orange.tpms.helper;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.util.ArrayList;
import java.util.List;

import bean.hardware.SensorDataBean;

public class ProgramSensorHelper extends BaseHelper {

    private static final String TAG = "ReadSensorHelper";
    //防止finish多次回调
    private boolean hasFinish = false;
    //定时器
    HandlerHelper handlerHelper = new HandlerHelper();
    /**
     * 触发读取传感器
     */
    public void trigger(String hex){
        preRequestNext();
        if(HardwareApp.getInstance().isEnableHareware()){//开启硬件
            HardwareApp.getInstance().sensorHandler.readSensor(1, hex, sensorDataBean -> {
                Rocket.writeOuterLog( sensorDataBean.toString());
                finishRequestNext();
                if(TextUtils.isEmpty(sensorDataBean.getSensor_id())){
                    failedRequestNext("读取失败");
                    return;
                }
                onStrggerSuccessNext(sensorDataBean.getSensor_id());
            },"00");
        }else{ //关闭硬件模拟数据
            new Handler().postDelayed(() -> {
                finishRequestNext();
                boolean success = Math.random() > 0.5f;
                if(success){
                    onStrggerSuccessNext("SR26dhDF");
                }else{
                    failedRequestNext("模拟失败");
                }
            },1000);
        }
    }

    /**
     * 烧录传感器
     */
    public void writeSensor(int number,String hex,String name){
        Log.v("yhd-", "writeSensor:"+"number:"+number+" hex:"+hex);
        //防止finish多次回调
        hasFinish = false;
        if(!HardwareApp.getInstance().isEnableHareware()) {//开启硬件
            failedRequestNext("硬件关闭，模拟失败");
            return;
        }
        preRequestNext();
        int status = HardwareApp.getInstance().sensorHandler.writeSensorFirmwareWithProgress(number, new SensorHandler.FlashWriteProgress() {

            @Override
            public void start(int total) {
                Rocket.writeOuterLog("烧录传感器-start:"+total);
                Log.v("yhd-", String.format("total bytes %s, has start", total));
            }

            @Override
            public void progress(int progress, int total) {
                Rocket.writeOuterLog("烧录传感器-progress:"+String.format("total bytes %s, progress: %s", total, progress));
                Log.v("yhd-", String.format("total bytes %s, progress: %s", total, progress));
                float finalProgress = (float) progress/total * 100;
                if(finalProgress >= 100){
                    finish(total);
                }else{
                    progressNext(finalProgress);
                }
            }

            @Override
            public void finish(int total) {
                Rocket.writeOuterLog("烧录传感器-finish:"+total);
                Log.v("yhd-", String.format("total bytes %s, has finish", total));
                if(!hasFinish){
                    hasFinish = true;
                    onCheckProgramNext();
                    //读到的传感器列表，先存起来
                    List<SensorDataBean> sensorDataBeans = new ArrayList<>();
                    //延迟读,不然烧录完不是最终状态
                    HardwareApp.getInstance().sensorHandler.readSensor(number, hex, sensorDataBean -> {
                        sensorDataBeans.add(sensorDataBean);
                        finishRequestNext();
                        if(sensorDataBean != null && !TextUtils.isEmpty(sensorDataBean.getSensor_id())){
                            // 确认烧录完成
                            onProgramSuccessNext(sensorDataBean.getSensor_id(),true);
                        }
                    },"00");
                    //开始计时
                    handlerHelper.postDelay(3000);
                    //超时处理
                    handlerHelper.setOnTimeListener(() -> {
                        //没有读完全部传感器就超时
                        if(sensorDataBeans.size() != number){
                            onCheckTimeoutNext(sensorDataBeans);
                        }
                    });
                }
            }

            @Override
            public void fail(int errcode, int start, int total) {
                Rocket.writeOuterLog("烧录传感器-fail:"+String.format("total bytes %s, fail on %s, errcode is %s", total,
                        start, errcode));
                finishRequestNext();
                onProgramSuccessNext(null,false);
                Log.v("yhd-", String.format("total bytes %s, fail on %s, errcode is %s", total,
                        start, errcode));
            }
        },name);
        Rocket.writeOuterLog("烧录传感器状态码:"+status);
    }

    /* ***************************** StrggerSuccess ***************************** */

    private OnStrggerSuccessListener onStrggerSuccessListener;

    // 接口类 -> OnStrggerSuccessListener
    public interface OnStrggerSuccessListener {
        void onStrggerSuccess(String sensorid);
    }

    // 对外暴露接口 -> setOnStrggerSuccessListener
    public void setOnStrggerSuccessListener(OnStrggerSuccessListener onStrggerSuccessListener) {
        this.onStrggerSuccessListener = onStrggerSuccessListener;
    }

    // 内部使用方法 -> StrggerSuccessNext
    private void onStrggerSuccessNext(String sensorid) {
        if (onStrggerSuccessListener != null) {
            runMainThread(() -> onStrggerSuccessListener.onStrggerSuccess(sensorid));
        }
    }

    /* *********************************  OnProgressListener  ************************************** */

    private OnProgressListener onProgressListener;

    public void progressNext(float progress) {
        if (onProgressListener != null) {
            runMainThread(() -> onProgressListener.onProgress(progress));
        }
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public interface OnProgressListener {
        void onProgress(float progress);
    }

    /* ***************************** ProgramSuccess ***************************** */

    private OnProgramSuccessListener onProgramSuccessListener;

    // 接口类 -> OnProgramSuccessListener
    public interface OnProgramSuccessListener {
        void onProgramSuccess(String sensorid,boolean success);
    }

    // 对外暴露接口 -> setOnProgramSuccessListener
    public void setOnProgramSuccessListener(OnProgramSuccessListener onProgramSuccessListener) {
        this.onProgramSuccessListener = onProgramSuccessListener;
    }

    // 内部使用方法 -> ProgramSuccessNext
    private void onProgramSuccessNext(String sensorid,boolean success) {
        if (onProgramSuccessListener != null) {
            runMainThread(() -> onProgramSuccessListener.onProgramSuccess(sensorid,success));
        }
    }

    /* ***************************** CheckProgram ***************************** */

    private OnCheckProgramListener onCheckProgramListener;

    // 接口类 -> OnCheckProgramListener
    public interface OnCheckProgramListener {
        void onCheckProgram();
    }

    // 对外暴露接口 -> setOnCheckProgramListener
    public void setOnCheckProgramListener(OnCheckProgramListener onCheckProgramListener) {
        this.onCheckProgramListener = onCheckProgramListener;
    }

    // 内部使用方法 -> CheckProgramNext
    private void onCheckProgramNext() {
        if (onCheckProgramListener != null) {
            runMainThread(() -> onCheckProgramListener.onCheckProgram());
        }
    }

    /* ***************************** CheckTimeout ***************************** */

    private OnCheckTimeoutListener onCheckTimeoutListener;

    // 接口类 -> OnCheckTimeoutListener
    public interface OnCheckTimeoutListener {
        void onCheckTimeout(List<SensorDataBean> sensorDataBeans);
    }

    // 对外暴露接口 -> setOnCheckTimeoutListener
    public void setOnCheckTimeoutListener(OnCheckTimeoutListener onCheckTimeoutListener) {
        this.onCheckTimeoutListener = onCheckTimeoutListener;
    }

    // 内部使用方法 -> CheckTimeoutNext
    private void onCheckTimeoutNext(List<SensorDataBean> SensorDataBeans) {
        if (onCheckTimeoutListener != null) {
            runMainThread(() -> onCheckTimeoutListener.onCheckTimeout(SensorDataBeans));
        }
    }
}
