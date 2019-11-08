package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

/**
 * 信息类的Bean
 * Created by haide.yin() on 2019/4/16 10:27.
 */
public class InformationBean extends RoBean {

    private String title;//标题
    private String information;//描述

    public InformationBean(String title, String information) {
        this.title = title;
        this.information = information;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "InformationBean{" +
                "title='" + title + '\'' +
                ", information='" + information + '\'' +
                '}';
    }
}
