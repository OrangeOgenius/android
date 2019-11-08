package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

public class TitleBean extends RoBean {
    private String title;//标题

    public TitleBean(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "TitleBean{" +
                "title='" + title + '\'' +
                '}';
    }
}
