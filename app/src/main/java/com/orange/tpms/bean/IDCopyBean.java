package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

public class IDCopyBean extends RoBean {

    private String position;//车轮位置
    private String sensorid;//id
    private String psi;//psi
    private String temp;//温度
    private String bat;//电池
    private boolean editable;//是否可以编辑

    public IDCopyBean(String position, String sensorid, String psi, String temp, String bat, boolean editable) {
        this.position = position;
        this.sensorid = sensorid;
        this.psi = psi;
        this.temp = temp;
        this.bat = bat;
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "IDCopyBean{" +
                "position='" + position + '\'' +
                ", sensorid='" + sensorid + '\'' +
                ", psi='" + psi + '\'' +
                ", temp='" + temp + '\'' +
                ", bat='" + bat + '\'' +
                ", editable=" + editable +
                '}';
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getPsi() {
        return psi;
    }

    public void setPsi(String psi) {
        this.psi = psi;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getBat() {
        return bat;
    }

    public void setBat(String bat) {
        this.bat = bat;
    }

}
