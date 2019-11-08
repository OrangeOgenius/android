package bean.server.rep;

/**
 * 获取指定版本的固件
 * Created by john on 2019/3/28.
 */
public class GetMcuUpdateUrlBeanRsp {

    private String Url;
    public void setUrl(String AUrl) {
        this.Url = AUrl;
    }
    public String getUrl() {
        return Url;
    }


}
