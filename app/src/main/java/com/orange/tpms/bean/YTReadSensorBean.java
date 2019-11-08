package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

/**
 * 宇通读取传感器的Bean
 * Created by haide.yin() on 2019/6/25 14:04.
 */
public class YTReadSensorBean extends RoBean {

    private boolean isTitle;
    private String name;
    private String idNumber;
    private String tirePress;
    private String tireTemp;
    private String batName;
    private int batLevel;

    public YTReadSensorBean(boolean isTitle, String name, String idNumber, String tirePress, String tireTemp, String batName, int batLevel) {
        this.isTitle = isTitle;
        this.name = name;
        this.idNumber = idNumber;
        this.tirePress = tirePress;
        this.tireTemp = tireTemp;
        this.batName = batName;
        this.batLevel = batLevel;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getTirePress() {
        return tirePress;
    }

    public void setTirePress(String tirePress) {
        this.tirePress = tirePress;
    }

    public String getTireTemp() {
        return tireTemp;
    }

    public void setTireTemp(String tireTemp) {
        this.tireTemp = tireTemp;
    }

    public String getBatName() {
        return batName;
    }

    public void setBatName(String batName) {
        this.batName = batName;
    }

    public int getBatLevel() {
        return batLevel;
    }

    public void setBatLevel(int batLevel) {
        this.batLevel = batLevel;
    }

    @Override
    public String toString() {
        return "YTReadSensorBean{" +
                "isTitle=" + isTitle +
                ", name='" + name + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", tirePress='" + tirePress + '\'' +
                ", tireTemp='" + tireTemp + '\'' +
                ", batName='" + batName + '\'' +
                ", batLevel=" + batLevel +
                '}';
    }
}
