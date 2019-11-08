package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

import bean.mmy.MMyBean;

public class ProgramItemBean extends RoBean {

    public static int STATE_NORMAL = 0;//正常状态
    public static int STATE_SUCCESS = 1;//成功状态
    public static int STATE_FAILED = 2;//失败状态
    public static int STATE_HIDE= 3;//隐藏状态

    private String sensorid;//传感器id
    private int state;//状态
    private boolean editable;//是否可以编辑
    private boolean showIndex;//是否显示序号

    public ProgramItemBean(boolean showIndex,String sensorid, int state, boolean editable) {
        this.showIndex = showIndex;
        this.sensorid = sensorid;
        this.state = state;
        this.editable = editable;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isShowIndex() {
        return showIndex;
    }

    public void setShowIndex(boolean showIndex) {
        this.showIndex = showIndex;
    }

    @Override
    public String toString() {
        return "ProgramItemBean{" +
                "sensorid='" + sensorid + '\'' +
                ", state=" + state +
                ", editable=" + editable +
                ", showIndex=" + showIndex +
                '}';
    }
}
