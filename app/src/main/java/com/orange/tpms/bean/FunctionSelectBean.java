package com.orange.tpms.bean;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * 功能选择Adapter的Bean
 * Created by haide.yin() on 2019/9/6 9:16.
 */
public class FunctionSelectBean {

    private @DrawableRes int bgSelector;//背景的选择器
    private boolean selectable;//是否需要屏蔽灰化
    private String title;//标题
    private Class targetClass;//目标跳转的Class
    private Serializable targetObject;//是否携带数据
    private boolean hide;//是否隐藏

    public FunctionSelectBean() {

    }

    public FunctionSelectBean(int bgSelector, boolean selectable, String title, Class targetClass) {
        this.bgSelector = bgSelector;
        this.selectable = selectable;
        this.title = title;
        this.targetClass = targetClass;
    }

    public FunctionSelectBean(int bgSelector, boolean selectable, String title, Class targetClass, boolean hide) {
        this.bgSelector = bgSelector;
        this.selectable = selectable;
        this.title = title;
        this.targetClass = targetClass;
        this.hide = hide;
    }

    public FunctionSelectBean(int bgSelector, boolean selectable, String title, Class targetClass, Serializable targetObject, boolean hide) {
        this.bgSelector = bgSelector;
        this.selectable = selectable;
        this.title = title;
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.hide = hide;
    }

    public Serializable getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Serializable targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public String toString() {
        return "FunctionSelectBean{" +
                "bgSelector=" + bgSelector +
                ", selectable=" + selectable +
                ", title='" + title + '\'' +
                ", targetClass=" + targetClass +
                ", targetObject=" + targetObject +
                ", hide=" + hide +
                '}';
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public int getBgSelector() {
        return bgSelector;
    }

    public void setBgSelector(int bgSelector) {
        this.bgSelector = bgSelector;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

}
