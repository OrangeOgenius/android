package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

import bean.mmy.MMyBean;

/**
 * 我的最爱页面传递消息体
 * Created by haide.yin() on 2019/4/3 17:19.
 */
public class FavouriteFragBean extends RoBean {

    private Class targetClass;//目标的Class
    private MMyBean mMyBean;//消息体

    public FavouriteFragBean() {

    }

    public FavouriteFragBean(Class targetClass, MMyBean mMyBean) {
        this.targetClass = targetClass;
        this.mMyBean = mMyBean;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public MMyBean getmMyBean() {
        return mMyBean;
    }

    public void setmMyBean(MMyBean mMyBean) {
        this.mMyBean = mMyBean;
    }

    @Override
    public String toString() {
        return "FavouriteFragBean{" +
                "targetClass=" + targetClass +
                ", mMyBean=" + mMyBean +
                '}';
    }
}
