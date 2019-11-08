package com.orange.tpms.bean;

/**
 * 请求参数
 */
public class CheckVersionSensorReq {
    private String serialNum;

    public void setSerialNum (String serialNum) {
        this.serialNum = serialNum;
    }

    public String getSerialNum () {
        return this.serialNum;
    }
}
