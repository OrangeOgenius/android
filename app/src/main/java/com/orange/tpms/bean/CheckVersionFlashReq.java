package com.orange.tpms.bean;

/**
 * Flash版本请求
 */
public class CheckVersionFlashReq {
    private String serialNum;

    public void setSerialNum (String serialNum) {
        this.serialNum = serialNum;
    }

    public String getSerialNum () {
        return this.serialNum;
    }
}
