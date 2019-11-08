package bean.server.rep;

/**
 * 获取所有的Mcu更新地址
 * Created by john on 2019/4/5.
 */

public class GetAllMcuUpdateUrlBeanRsp {
    private String AUrl;
    private String BUrl;
    private String CUrl;
    private String DUrl;
    public void setAUrl(String AUrl) {
        this.AUrl = AUrl;
    }
    public String getAUrl() {
        return AUrl;
    }

    public void setBUrl(String BUrl) {
        this.BUrl = BUrl;
    }
    public String getBUrl() {
        return BUrl;
    }

    public void setCUrl(String CUrl) {
        this.CUrl = CUrl;
    }
    public String getCUrl() {
        return CUrl;
    }

    public void setDUrl(String DUrl) {
        this.DUrl = DUrl;
    }
    public String getDUrl() {
        return DUrl;
    }
}
