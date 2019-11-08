package com.orange.tpms.helper;

import android.os.Handler;
import android.util.Log;

import com.de.rocket.Rocket;
import com.orange.tpms.bean.IDCopyFragBean;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.activity.MainActivity;

public class CopyIDHelper extends BaseHelper {

    private static final String TAG = "ReadSensorHelper";

    /**
     * 触发扫码
     */
    public void trigger(String hex){
        Rocket.writeOuterLog("读取传感器-trigger");
        preRequestNext();
        if(HardwareApp.getInstance().isEnableHareware()){//开启硬件
            HardwareApp.getInstance().sensorHandler.readSensor(1, hex, sensorDataBean -> {
                Rocket.writeOuterLog(sensorDataBean.toString());
                finishRequestNext();
                // TODO: 2019/9/5 zeqiang psi 这个字段没有
                onStrggerSuccessNext(
                        sensorDataBean.getSensor_id(),
                        sensorDataBean.getV()+"",
                        sensorDataBean.getTemp()+"",
                        sensorDataBean.getBatteryLevel()+"");
            },"00");
        }else{ //关闭硬件模拟数据
            new Handler().postDelayed(() -> {
                finishRequestNext();
                boolean success = Math.random() > 0.5f;
                if(success){
                    onStrggerSuccessNext("SR2358HD","6","30","60");
                }else{
                    failedRequestNext("模拟失败");
                }
            },1000);
        }
    }

    /**
     * Copy ID
     */
    public void copyid(){
        Rocket.writeOuterLog("Copy ID-copyid");
        if(!HardwareApp.getInstance().isEnableHareware()) {//开启硬件
            failedRequestNext("硬件关闭，模拟失败");
            return;
        }

        if( MainActivity.SensorList != null
                && MainActivity.NewSensorList != null
                && MainActivity.NewSensorList.size() == MainActivity.SensorList.size()
                && MainActivity.SensorList.size() > 0){
            if(MainActivity.SensorList.size() == 1){
                String originalSensorid = MainActivity.SensorList.get(0);
                String newSensorid = MainActivity.NewSensorList.get(0);
                idCopy(0,originalSensorid, newSensorid, null);
            }else if(MainActivity.SensorList.size() == 2){
                String originalSensorid1 = MainActivity.SensorList.get(0);
                String newSensorid1 = MainActivity.NewSensorList.get(0);
                String originalSensorid2 = MainActivity.SensorList.get(1);
                String newSensorid2 = MainActivity.NewSensorList.get(1);
                idCopy(0,originalSensorid1, newSensorid1, ret -> idCopy(1,originalSensorid2, newSensorid2, null));
            }else if(MainActivity.SensorList.size() == 3){
                String originalSensorid1 = MainActivity.SensorList.get(0);
                String newSensorid1 = MainActivity.NewSensorList.get(0);
                String originalSensorid2 = MainActivity.SensorList.get(1);
                String newSensorid2 = MainActivity.NewSensorList.get(1);
                String originalSensorid3 = MainActivity.SensorList.get(2);
                String newSensorid3 = MainActivity.NewSensorList.get(2);
                idCopy(0,originalSensorid1, newSensorid1, ret1 -> idCopy(1,originalSensorid2, newSensorid2, ret2 -> idCopy(2,originalSensorid3, newSensorid3, null)));
            }else if(MainActivity.SensorList.size() == 4){
                String originalSensorid1 = MainActivity.SensorList.get(0);
                String newSensorid1 = MainActivity.NewSensorList.get(0);
                String originalSensorid2 = MainActivity.SensorList.get(1);
                String newSensorid2 = MainActivity.NewSensorList.get(1);
                String originalSensorid3 = MainActivity.SensorList.get(2);
                String newSensorid3 = MainActivity.NewSensorList.get(2);
                String originalSensorid4 = MainActivity.SensorList.get(3);
                String newSensorid4 = MainActivity.NewSensorList.get(3);
                idCopy(0,originalSensorid1, newSensorid1, ret1 -> idCopy(1,originalSensorid2, newSensorid2, ret2 -> idCopy(2,originalSensorid3, newSensorid3, ret3 -> idCopy(3,originalSensorid4, newSensorid4, null))));
            }
        }
    }

    /**
     * Copy ID
     */
    private void idCopy(int index,String originalSensorid,String newSensorid,SensorSource.SensorRespondCB sensorRespondCB){
        preRequestNext();
        // 将第一个拷到第二个
        HardwareApp.getInstance().sensorHandler.copySensorId(originalSensorid, newSensorid, ret -> {
            Rocket.writeOuterLog("Copy ID-copySensorId:"+"originalSensorid:"+originalSensorid+" newSensorid:"+newSensorid+" ret:"+ret);
            finishRequestNext();
            if(ret == 0){
                onIDCopySuccessNext(index,true);
            }else{
                onIDCopySuccessNext(index,false);
            }
            if(sensorRespondCB != null){
                sensorRespondCB.receive(ret);
            }
        });
    }

    /* ***************************** StrggerSuccess ***************************** */

    private OnStrggerSuccessListener onStrggerSuccessListener;

    // 接口类 -> OnStrggerSuccessListener
    public interface OnStrggerSuccessListener {
        void onStrggerSuccess(String sensorid,String psi,String temp,String bat);
    }

    // 对外暴露接口 -> setOnStrggerSuccessListener
    public void setOnStrggerSuccessListener(OnStrggerSuccessListener onStrggerSuccessListener) {
        this.onStrggerSuccessListener = onStrggerSuccessListener;
    }

    // 内部使用方法 -> StrggerSuccessNext
    private void onStrggerSuccessNext(String sensorid,String psi,String temp,String bat) {
        if (onStrggerSuccessListener != null) {
            runMainThread(() -> onStrggerSuccessListener.onStrggerSuccess(sensorid,psi,temp,bat));
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

    /* ***************************** IDCopySuccess ***************************** */

    private OnIDCopySuccessListener onIDCopySuccessListener;

    // 接口类 -> OnIDCopySuccessListener
    public interface OnIDCopySuccessListener {
        void onIDCopySuccess(int index,boolean success);
    }

    // 对外暴露接口 -> setOnIDCopySuccessListener
    public void setOnIDCopySuccessListener(OnIDCopySuccessListener onIDCopySuccessListener) {
        this.onIDCopySuccessListener = onIDCopySuccessListener;
    }

    // 内部使用方法 -> IDCopySuccessNext
    private void onIDCopySuccessNext(int index,boolean success) {
        if (onIDCopySuccessListener != null) {
            runMainThread(() -> onIDCopySuccessListener.onIDCopySuccess(index,success));
        }
    }
}
