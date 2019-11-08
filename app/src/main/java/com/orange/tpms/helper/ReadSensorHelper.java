package com.orange.tpms.helper;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.de.rocket.Rocket;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.util.ArrayList;
import java.util.List;

import bean.hardware.SensorDataBean;

public class ReadSensorHelper extends BaseHelper {

    private static final String TAG = "ReadSensorHelper";

    //是否已经处理
    //boolean isHandle = false;

    /**
     * 读取传感器
     */
    public void readSensor(int number,String mmyCode,String a){
        Rocket.writeOuterLog("读取传感器-readSensor");
        preRequestNext();
        if(HardwareApp.getInstance().isEnableHareware()){//开启硬件
            //读到的传感器列表，先存起来
            //List<SensorDataBean> sensorDataBeans = new ArrayList<>();
            //isHandle = false;
            HardwareApp.getInstance().sensorHandler.readSensor(number, mmyCode, sensorDataBean -> {
                Rocket.writeOuterLog( sensorDataBean.toString());
                /*sensorDataBeans.add(sensorDataBean);
                if(sensorDataBeans.size() == number){
                    handleSensorDataBean(sensorDataBeans);
                }*/
                finishRequestNext();
                if(TextUtils.isEmpty(sensorDataBean.getSensor_id())){
                    onReadFailedNext();
                }else{
                    readSensorNext(sensorDataBean);
                }
            },a);
            //指定后执行
            /*new Thread(() -> {
                try {
                    Thread.sleep(number * 2000);
                    handleSensorDataBean(sensorDataBeans);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();*/
        }else{ //关闭硬件模拟数据
            new Handler().postDelayed(() -> {
                finishRequestNext();
                boolean success = Math.random() > 0.5f;
                if(success){
                    SensorDataBean tempSensorDataBean = new SensorDataBean();
                    tempSensorDataBean.setTemp(5);
                    tempSensorDataBean.setBatteryLevel(80);
                    tempSensorDataBean.setV(5);
                    tempSensorDataBean.setSensor_id("SR26dhDF");
                    tempSensorDataBean.setKpa(5);
                    readSensorNext(tempSensorDataBean);
                }else{
                    onReadFailedNext();
                }
            },1000);
        }
    }

    /**
     * 处理回调
     */
    private void handleSensorDataBean(List<SensorDataBean> sensorDataBeans){
        finishRequestNext();
        for(SensorDataBean sensorDataBean : sensorDataBeans){
            if(TextUtils.isEmpty(sensorDataBean.getSensor_id())){
                onReadFailedNext();
            }else{
                readSensorNext(sensorDataBean);
            }
        }
    }

    /* *********************************  Read Sensor  ************************************** */

    private OnReadSensorListener onReadSensorListener;

    public void readSensorNext(SensorDataBean sensorDataBean){
        if(onReadSensorListener != null){
            runMainThread(() -> onReadSensorListener.onReadSensor(sensorDataBean));
        }
    }

    public void setReadSensorListener(OnReadSensorListener onReadSensorListener){
        this.onReadSensorListener = onReadSensorListener;
    }

    public interface OnReadSensorListener{
        void onReadSensor(SensorDataBean sensorDataBean);
    }

    /* ***************************** ReadFailed ***************************** */

    private OnReadFailedListener onReadFailedListener;

    // 接口类 -> OnReadFailedListener
    public interface OnReadFailedListener {
        void onReadFailed();
    }

    // 对外暴露接口 -> setOnReadFailedListener
    public void setOnReadFailedListener(OnReadFailedListener onReadFailedListener) {
        this.onReadFailedListener = onReadFailedListener;
    }

    // 内部使用方法 -> ReadFailedNext
    private void onReadFailedNext() {
        if (onReadFailedListener != null) {
            runMainThread(() -> onReadFailedListener.onReadFailed());
        }
    }
}
