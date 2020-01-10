package com.orange.tpms.bean;

import android.text.Editable;
import com.de.rocket.bean.RoBean;

public class IDCopyDetailBean extends RoBean {

    public static int STATE_HIDE = -1;//隐藏状态
    public static int STATE_NORMAL = 0;//正常状态
    public static int STATE_SUCCESS = 1;//成功状态
    public static int STATE_FAILED = 2;//失败状态

    private String position;
    private String originalid;
    private String newid;
    private String checkTitle;
    private int state;
    private boolean editable;

    public IDCopyDetailBean(String position, String originalid, String newid, String checkTitle, int state) {
        this.position = position;
        this.originalid = originalid;
        this.newid = newid;
        this.checkTitle = checkTitle;
        this.state = state;
    }
public void setEditable(Boolean a){
        editable=a;
}
public Boolean getEditable(){
        return editable;
}
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOriginalid() {
        return originalid;
    }

    public void setOriginalid(String originalid) {
        this.originalid = originalid;
    }

    public String getNewid() {
        return newid;
    }

    public void setNewid(String newid) {
        this.newid = newid;
    }

    public String getCheckTitle() {
        return checkTitle;
    }

    public void setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "IDCopyDetailBean{" +
                "position='" + position + '\'' +
                ", originalid='" + originalid + '\'' +
                ", newid='" + newid + '\'' +
                ", checkTitle='" + checkTitle + '\'' +
                ", check='" + state + '\'' +
                '}';
    }
}
