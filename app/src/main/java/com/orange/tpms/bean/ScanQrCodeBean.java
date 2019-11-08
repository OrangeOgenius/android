package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

/**
 * 二维码扫描的Bean
 * Created by haide.yin() on 2019/4/3 17:19.
 */
public class ScanQrCodeBean extends RoBean {

    public static int TYPE_MMY = 0;//扫描MMY
    public static int TYPE_SENSORID = 1;//扫描Sensorid

    private SensorQrCodeBean sensorQrCodeBean;//扫描传感器的二维码Bean
    private MMYQrCodeBean mmyQrCodeBean;//扫描MMY二维码的Bean
    private int scanType;//扫描类型

    public ScanQrCodeBean(int scanType) {
        this.scanType = scanType;
    }

    public ScanQrCodeBean(SensorQrCodeBean sensorQrCodeBean, MMYQrCodeBean mmyQrCodeBean, int scanType) {
        this.sensorQrCodeBean = sensorQrCodeBean;
        this.mmyQrCodeBean = mmyQrCodeBean;
        this.scanType = scanType;
    }

    public SensorQrCodeBean getSensorQrCodeBean() {
        return sensorQrCodeBean;
    }

    public void setSensorQrCodeBean(SensorQrCodeBean sensorQrCodeBean) {
        this.sensorQrCodeBean = sensorQrCodeBean;
    }

    public MMYQrCodeBean getMmyQrCodeBean() {
        return mmyQrCodeBean;
    }

    public void setMmyQrCodeBean(MMYQrCodeBean mmyQrCodeBean) {
        this.mmyQrCodeBean = mmyQrCodeBean;
    }

    public int getScanType() {
        return scanType;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
    }

    @Override
    public String toString() {
        return "ScanQrCodeBean{" +
                "sensorQrCodeBean=" + sensorQrCodeBean +
                ", mmyQrCodeBean=" + mmyQrCodeBean +
                ", scanType=" + scanType +
                '}';
    }
}
