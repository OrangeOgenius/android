package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;
import com.orange.tpms.helper.LanguageHelper;

public class LanguageBean extends RoBean {

    private String name;//语言的描述
    private LanguageHelper.SURPORT_LANGUAGE surportLanguage;//支持的语言列表

    public LanguageBean(String name, LanguageHelper.SURPORT_LANGUAGE surportLanguage) {
        this.name = name;
        this.surportLanguage = surportLanguage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LanguageHelper.SURPORT_LANGUAGE getSurportLanguage() {
        return surportLanguage;
    }

    public void setSurportLanguage(LanguageHelper.SURPORT_LANGUAGE surportLanguage) {
        this.surportLanguage = surportLanguage;
    }

    @Override
    public String toString() {
        return "LanguageBean{" +
                "name='" + name + '\'' +
                ", surportLanguage=" + surportLanguage +
                '}';
    }
}
