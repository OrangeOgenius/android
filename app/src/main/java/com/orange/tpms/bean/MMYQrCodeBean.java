package com.orange.tpms.bean;

import android.text.TextUtils;

/**
 * MMY二维码的内容
 * Created by haide.yin() on 2019/9/16 13:11.
 */
public class MMYQrCodeBean {

    private String mmyNumber;
    private String mmyCode;

    public MMYQrCodeBean() {

    }

    public MMYQrCodeBean(String mmyNumber, String mmyCode) {
        this.mmyNumber = mmyNumber;
        this.mmyCode = mmyCode;
    }

    public String getMmyNumber() {
        return mmyNumber;
    }

    public void setMmyNumber(String mmyNumber) {
        this.mmyNumber = mmyNumber;
    }

    public String getMmyCode() {
        return mmyCode;
    }

    public void setMmyCode(String mmyCode) {
        this.mmyCode = mmyCode;
    }

    @Override
    public String toString() {
        return "MMYQrCodeBean{" +
                "mmyNumber='" + mmyNumber + '\'' +
                ", mmyCode='" + mmyCode + '\'' +
                '}';
    }

    /**
     * MMY二维码转bean
     */
    public static MMYQrCodeBean toQRcodeBean(String content){
        MMYQrCodeBean mmyQrCodeBean = new MMYQrCodeBean();
        if(!TextUtils.isEmpty(content)&&content.contains("**")){
            String[] dataArray = content.split("\\*\\*");
            if(dataArray.length == 2){
                mmyQrCodeBean.setMmyNumber(dataArray[0]);
                mmyQrCodeBean.setMmyCode(dataArray[1]);
            }
        }
        return mmyQrCodeBean;
    }
}
