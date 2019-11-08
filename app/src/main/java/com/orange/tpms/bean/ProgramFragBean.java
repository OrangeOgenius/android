package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

import bean.mmy.MMyBean;

/**
 * 烧录传感器页面传递消息体
 * Created by haide.yin() on 2019/4/3 17:19.
 */
public class ProgramFragBean extends RoBean {

    private int number;//烧录的传感器数
    private MMyBean mMyBean;//MMYBean

    public ProgramFragBean() {

    }

    public ProgramFragBean(int number) {
        this.number = number;
    }

    public ProgramFragBean(int number, MMyBean mMyBean) {
        this.number = number;
        this.mMyBean = mMyBean;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public MMyBean getmMyBean() {
        return mMyBean;
    }

    public void setmMyBean(MMyBean mMyBean) {
        this.mMyBean = mMyBean;
    }

    @Override
    public String toString() {
        return "ProgramFragBean{" +
                "number=" + number +
                ", mMyBean=" + mMyBean +
                '}';
    }
}
