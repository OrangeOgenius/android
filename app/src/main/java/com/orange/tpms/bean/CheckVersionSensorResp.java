package com.orange.tpms.bean;

/**
 * 响应参数
 */
public class CheckVersionSensorResp {
    private String CurrentVersion;
    private String LastVersion;
    private boolean IsNeedUpdate;
    private String FileUrl;
    private String RealFileUrl;
    private String FileName;

    public void setCurrentVersion (String currentVersion) {
        this.CurrentVersion = currentVersion;
    }

    public void setLastVersion (String lastVersion) {
        this.LastVersion = lastVersion;
    }

    public void setFileUrl (String fileUrl) {
        this.FileUrl = fileUrl;
    }

    public void setRealFileUrl (String realFileUrl) {
        this.RealFileUrl = realFileUrl;
    }

    public void setFileName (String fileName) {
        this.FileName = fileName;
    }

    public void setNeedUpdate (boolean needUpdate) {
        this.IsNeedUpdate = needUpdate;
    }

    public String getCurrentVersion () {
        return this.CurrentVersion;
    }

    public String getLastVersion () {
        return this.LastVersion;
    }

    public String getFileUrl () {
        return this.FileUrl;
    }

    public String getRealFileUrl () {
        return this.RealFileUrl;
    }

    public String getFileName () {
        return this.FileName;
    }

    public boolean getNeedUpdate () {
        return this.IsNeedUpdate;
    }
}
