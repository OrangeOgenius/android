package com.orange.tpms.bean;

import bean.mmy.MMyBean;
import com.de.rocket.bean.RoBean;

import java.util.List;

/**
 * IDCopy携带的数据
 * Created by haide.yin() on 2019/9/5 9:29.
 */
public class IDCopyFragBean extends RoBean {

    private List<String> orignalSendorid;
    private List<String> newSendorid;
    private MMyBean mMyBean;

    public IDCopyFragBean() {

    }

    public IDCopyFragBean(List<String> orignalSendorid, List<String> newSendorid, MMyBean mMyBean) {
        this.orignalSendorid = orignalSendorid;
        this.newSendorid = newSendorid;
        this.mMyBean = mMyBean;
    }

    public MMyBean getmMyBean() {
        return mMyBean;
    }

    public void setmMyBean(MMyBean mMyBean) {
        this.mMyBean = mMyBean;
    }

    public List<String> getOrignalSendorid() {
        return orignalSendorid;
    }

    public void setOrignalSendorid(List<String> orignalSendorid) {
        this.orignalSendorid = orignalSendorid;
    }

    public List<String> getNewSendorid() {
        return newSendorid;
    }

    public void setNewSendorid(List<String> newSendorid) {
        this.newSendorid = newSendorid;
    }

    @Override
    public String toString() {
        return "IDCopyFragBean{" +
                "orignalSendorid=" + orignalSendorid +
                ", newSendorid=" + newSendorid +
                ", mMyBean=" + mMyBean +
                '}';
    }
}
