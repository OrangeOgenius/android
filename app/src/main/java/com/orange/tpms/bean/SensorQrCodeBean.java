package com.orange.tpms.bean;

import android.text.TextUtils;
import com.de.rocket.bean.RoBean;

public class SensorQrCodeBean extends RoBean {

    private String sensorID;//sensor id
    private String oddNumbver;//途程单号
    private String model;//型号

    public SensorQrCodeBean() {

    }

    public SensorQrCodeBean(String sensorID, String oddNumbver, String model) {
        this.sensorID = sensorID;
        this.oddNumbver = oddNumbver;
        this.model = model;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getOddNumbver() {
        return oddNumbver;
    }

    public void setOddNumbver(String oddNumbver) {
        this.oddNumbver = oddNumbver;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public static SensorQrCodeBean toQRcodeBean(String content){
        SensorQrCodeBean sensorQrCodeBean = new SensorQrCodeBean();
        if(!TextUtils.isEmpty(content)&&content.contains(":")){
            String[] dataArray = content.split(":");
            if(dataArray.length == 4){
                sensorQrCodeBean.setModel(dataArray[3]);
                sensorQrCodeBean.setOddNumbver(dataArray[2]);
                sensorQrCodeBean.setSensorID(dataArray[1]);
            }
        }else if(!TextUtils.isEmpty(content)&&content.contains("*")){
            sensorQrCodeBean.setSensorID(content.replace("*",""));
        }
        return sensorQrCodeBean;
    }

    @Override
    public String toString() {
        return "SensorQrCodeBean{" +
                "sensorID='" + sensorID + '\'' +
                ", oddNumbver='" + oddNumbver + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
