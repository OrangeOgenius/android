package com.orange.tpms.bean;

import bean.mmy.MMyBean;
import com.de.rocket.bean.RoBean;

/**
 * 我的最爱页面传递消息体
 * Created by haide.yin() on 2019/4/3 17:19.
 */
public class MMYFragBean extends RoBean {

    private Class targetClass;//目标的Class
    private MMyBean mMyBean;//MMYBean
    private MMYItemBean mmyItemBean;//消息体

    public MMYFragBean() {

    }

    public MMYFragBean(Class targetClass) {
        this.targetClass = targetClass;
    }

    public MMYFragBean(Class targetClass, MMyBean mMyBean, MMYItemBean mmyItemBean) {
        this.targetClass = targetClass;
        this.mMyBean = mMyBean;
        this.mmyItemBean = mmyItemBean;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public MMYItemBean getMmyItemBean() {
        return mmyItemBean;
    }

    public void setMmyItemBean(MMYItemBean mmyItemBean) {
        this.mmyItemBean = mmyItemBean;
    }

    public MMyBean getmMyBean() {
        return mMyBean;
    }

    public void setmMyBean(MMyBean mMyBean) {
        this.mMyBean = mMyBean;
    }

    @Override
    public String toString() {
        return "MMYFragBean{" +
                "targetClass=" + targetClass +
                ", mMyBean=" + mMyBean +
                ", mmyItemBean=" + mmyItemBean +
                '}';
    }
}
