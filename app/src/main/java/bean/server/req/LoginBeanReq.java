package bean.server.req;

/**
 * Created by john on 2019/3/27.
 */
public class LoginBeanReq {

    private String deviceSN;
    private String userId;
    private String passwd;

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }
    public String getDeviceSN() {
        return deviceSN;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public String getPasswd() {
        return passwd;
    }

    @Override
    public String toString() {
        return "LoginBeanReq{" +
                "deviceSN='" + deviceSN + '\'' +
                ", userId='" + userId + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}