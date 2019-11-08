package bean.server.rep;

/**
 * 最新版本
 */
public class LastestVersionRsp {

    private String mainVersion = "";
    private String aVersion = "";
    private String bVersion = "";
    private String cVersion = "";
    private String dVersion = "";
    private String sensorVersion = "";
    public void setMainVersion(String mainVersion) {
        this.mainVersion = mainVersion;
    }
    public String getMainVersion() {
        return mainVersion;
    }

    public void setAVersion(String aVersion) {
        this.aVersion = aVersion;
    }
    public String getAVersion() {
        return aVersion;
    }

    public void setBVersion(String bVersion) {
        this.bVersion = bVersion;
    }
    public String getBVersion() {
        return bVersion;
    }

    public void setCVersion(String cVersion) {
        this.cVersion = cVersion;
    }
    public String getCVersion() {
        return cVersion;
    }

    public void setDVersion (String dVersion ) {
        this.dVersion  = dVersion ;
    }
    public String getDVersion () {
        return dVersion ;
    }

    public void setSensorVersion(String sensorVersion) {
        this.sensorVersion = sensorVersion;
    }
    public String getSensorVersion() {
        return sensorVersion;
    }

}
