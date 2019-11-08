package com.orange.tpms.bean;

import com.de.rocket.bean.RoBean;

public class MMYItemBean extends RoBean {

    private String fileName;//文件名
    private String selectPath;//选中的文件路径
    private String normalPath;//正常显示的路径

    public MMYItemBean(String fileName, String selectPath, String normalPath) {
        this.fileName = fileName;
        this.selectPath = selectPath;
        this.normalPath = normalPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSelectPath() {
        return selectPath;
    }

    public void setSelectPath(String selectPath) {
        this.selectPath = selectPath;
    }

    public String getNormalPath() {
        return normalPath;
    }

    public void setNormalPath(String normalPath) {
        this.normalPath = normalPath;
    }

    @Override
    public String toString() {
        return "MMYItemBean{" +
                "fileName='" + fileName + '\'' +
                ", selectPath='" + selectPath + '\'' +
                ", normalPath='" + normalPath + '\'' +
                '}';
    }
}
