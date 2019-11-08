package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

/**
 * 宇通选择模型的Bean
 * Created by haide.yin() on 2019/6/25 14:04.
 */
public class YTSelectModelBean extends RoBean {

    public static final String TYPR_NORMAL = "普通车";
    public static final String TYPR_HIGH = "高端车";
    public static final String HEX_NORMAL = "70";
    public static final String HEX_HIGHT = "6F";

    private String name;//名称
    private boolean enable;//是否开放点击
    private String type;//高低端类型，0表示普通车，1表示高端车
    private String hex;//普通车: 70 高端车:6F

    public YTSelectModelBean() {

    }

    public YTSelectModelBean(String name, boolean enable, String type, String hex) {
        this.name = name;
        this.enable = enable;
        this.type = type;
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    @Override
    public String toString() {
        return "YTSelectModelBean{" +
                "name='" + name + '\'' +
                ", enable=" + enable +
                ", type='" + type + '\'' +
                ", hex='" + hex + '\'' +
                '}';
    }
}
