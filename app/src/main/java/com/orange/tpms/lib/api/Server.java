package com.orange.tpms.lib.api;

import android.content.Context;
import android.util.Log;

import com.orange.tpms.bean.CheckVersionCoverageReq;
import com.orange.tpms.bean.CheckVersionCoverageResp;
import com.orange.tpms.bean.CheckVersionFlashReq;
import com.orange.tpms.bean.CheckVersionFlashResp;
import com.orange.tpms.bean.CheckVersionSensorReq;
import com.orange.tpms.bean.CheckVersionSensorResp;
import com.orange.tpms.lib.api.conf.Constant;
import com.orange.tpms.lib.db.share.ServerShare;
import com.orange.tpms.lib.utils.StringUtils;

import org.apache.poi.util.XMLHelper;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import bean.server.rep.GetAllMcuUpdateUrlBeanRsp;
import bean.server.rep.GetMcuUpdateUrlBeanRsp;
import bean.server.rep.GetSensorUpdateUrlBeanRsp;
import bean.server.rep.GetSoftwareUrlBeanRsp;
import bean.server.rep.LastestVersionRsp;
import bean.server.rep.LoginBeanRsp;
import bean.server.rep.RegisterBeanRsp;
import bean.server.rep.ReportSensorInfoBeanRsp;
import bean.server.rep.SensorLogReportBeanRsp;
import bean.server.req.GetAllMcuUpdateUrlBeanReq;
import bean.server.req.GetMcuUpdateUrlBeanReq;
import bean.server.req.GetSensorUpdateUrlBeanReq;
import bean.server.req.GetSoftwareUrlBeanReq;
import bean.server.req.LastestVersionReq;
import bean.server.req.LoginBeanReq;
import bean.server.req.RegisterBeanReq;
import bean.server.req.ReportSensorInfoBeanReq;
import bean.server.req.SensorLogReportBeanReq;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * 服务端的接口, 包括
 * 1、登陆接口 (register)
 * 2、上传设备资料到服务器 (updateDevice)
 * 3、获取设备最新版本信息 (getSoftwareUpdateUrl)
 * 4、获取指定版本的固件 (getMcuUpdateUrl)
 * 5、获取传感器资料库 (getSensorUpdateUrl)
 * 6、上报奔溃日志 (sensorLogReport)
 * 7、更新传感器信息 (reportSensorInfo)
 * Created by john on 2019/3/27.
 */
public class Server extends BApi {

    public static String TAG = Server.class.getName();
    private static String SPLID_WORD = "\\|";

    private static int Timeout = 10000;     // 10s超时

    // 登陆凭证
    private String accessToken = "";
    private LastestVersionRsp lastestVersionRsp = null;
    private ErrorCB errorCB;        // 错误回调

    Context context = null;

    public Server (Context context) {
        this.context = context;
        this.lastestVersionRsp = new LastestVersionRsp();
        this.errorCB = new ErrorCB() {
            @Override
            public void onTokenInvalid(RetNode retNode) {

            }

            @Override
            public void onTimeout(RetNode retNode) {

            }

            @Override
            public void onOther(RetNode retNode) {

            }
        };
    }

    private void init () {
        // 如果没有网络且sharePrefrence里有非空token, 说明之前已经登陆过, 直接返回成功
        String accessToken = ServerShare.getInstance().getToken(context);
        String share_passwd = ServerShare.getInstance().getPasswd(context);
        String share_userId = ServerShare.getInstance().getUser(context);

        this.accessToken = accessToken;
    }

    public RetNode _post (String route, ArrayList<NameValuePair> formparams, int timeout, ArrayList<NameValuePair> headers) {
        String url = Constant.OrangeURl + route;
        return _req(url, formparams, timeout, headers, "post");
    }

    public RetNode _get (String route, ArrayList<NameValuePair> formparams, int timeout, ArrayList<NameValuePair> headers) {
        String url = Constant.OrangeURl + route;
        return _req(url, formparams, timeout, headers, "get");
    }

    public RetNode _webservice (String routeName, String xmlStr, int timeout) {
        // url
        String url = String.format("%s?op=%s", Constant.WebserviceURI, routeName);

        // headers
        ArrayList<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Content-Type", "text/xml;charset=utf-8"));
        headers.add(new BasicNameValuePair("SOAPAction", String.format("http://tempuri.org/%s", routeName)));

        // raw body
        ArrayList<NameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("body", String.format("" +
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "<soap:Body>\n" +
            "%s\n" +
            "</soap:Body>\n" +
            "</soap:Envelope>", xmlStr)));
        return _req(url, formparams, timeout, headers, "webservice");
    }

    /**
     * post请求
     * @param url
     * @param formparams
     * @param timeout 单位ms
     * @return
     */
    private RetNode _req (String url, ArrayList<NameValuePair> formparams, int timeout, ArrayList<NameValuePair> headers,
                          String type) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // 设置超时时间
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        httpClientBuilder.setDefaultRequestConfig(config);

        HttpClient httpClient = httpClientBuilder.build();

        // 打印输入
        Log.d(TAG + "_post", "=======================================");
        Log.d(TAG + "_post", url);
        Log.d(TAG + "_post", "***********input param*************");
        if (formparams != null) {
            for (int i = 0; i < formparams.size(); i++) {
                String name = formparams.get(i).getName();
                String value = formparams.get(i).getValue();
                Log.d(TAG + "_post", name + ": " + value);
            }
        }
        Log.d(TAG + "_post", "************header param***********");
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                String name = headers.get(i).getName();
                String value = headers.get(i).getValue();
                Log.d(TAG + "_post", name + ": " + value);
            }
        }
        Log.d(TAG + "_post", "-------------end--------------");

        HttpRequestBase httpReq = null;

        if (type == "post") {
            // 设置请求参数
            UrlEncodedFormEntity entity1 = null;
            try {
                entity1 = new UrlEncodedFormEntity(formparams, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (entity1 == null) {
                return null;
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity1);
            httpReq = httpPost;
        } else if (type == "webservice") {
            // 设置请求参数
            String body_str = "";
            if (formparams != null && !formparams.isEmpty()) {
                body_str = formparams.get(0).getValue();
            }
            StringEntity postingString = null;
            try {
                postingString = new StringEntity(body_str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(postingString);
            httpReq = httpPost;
        } else {
            httpReq = new HttpGet(url);
        }

        // 设置Header
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                NameValuePair valuePair = headers.get(i);
                String name = valuePair.getName();
                String value = valuePair.getValue();

                httpReq.setHeader(name, value);
            }
        }

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpReq);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RetNode retNode = new RetNode();

        if (httpResponse == null) {
            // 默认设置成超时
            retNode.status = 408;
            retNode.data = "";

            // 超时
            this.errorCB.onTimeout(retNode);
        } else if (httpResponse.getStatusLine().getStatusCode() == 401) {
            // 授权出错
            retNode.status = 401;
            retNode.data = "";

            // 授权出错回调
            this.errorCB.onTokenInvalid(retNode);
        } else if (httpResponse.getStatusLine().getStatusCode() != 200) {
            // 其他错误
            retNode.status = httpResponse.getStatusLine().getStatusCode();
            retNode.data = toString(httpResponse);

            // 其他错误
            this.errorCB.onOther(retNode);
        } else {
            // 打印输出
            retNode.data = toString(httpResponse);
            if (httpResponse != null) {
                retNode.status = httpResponse.getStatusLine().getStatusCode();
            }
        }

        Log.d(TAG+"_post", "-------------respond data--------------");
        Log.d(TAG+"_post", retNode.data);
        Log.d(TAG+"_post", "---------------------------------------");
        Log.d(TAG+"_post", "status: "+retNode.status);
        Log.d(TAG+"_post", "-------------data end--------------");
        return retNode;
    }

    /**
     * 获取登陆token
     * @return
     */
    private String getLoginAccessToken () {
        return this.accessToken;
    }

    private String toString (HttpResponse httpResponse) {
        String content = "";
        BufferedReader in = null;

        if (httpResponse == null) {
            return "";
        }
        try {
            in = new BufferedReader(new InputStreamReader(httpResponse.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            content = sb.toString();
        } catch (IOException e) {
            content = "";
        }
        return content;
    }

    /**
     * 异步请求
     */
    public void register (LoginBeanReq req, CB<Respond<LoginBeanRsp>> rsp_cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Respond<LoginBeanRsp> rsp = register(req);
                rsp_cb.onRsp(rsp);
            }
        }).start();
    }

    public void registerNewAccount (RegisterBeanReq req, CB<Respond<RegisterBeanRsp>> rsp_cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Respond<RegisterBeanRsp> rsp = registerNewAccount(req);
                rsp_cb.onRsp(rsp);
            }
        }).start();
    }

    /**
     * 1. token失效后的处理回调
     * 2. 请求超时后的处理回调
     */
    public void setErrorCallBack (ErrorCB cb) {
        this.errorCB = cb;
    }

    /**
     * 注册 (同步)
     * @return
     */
    public Respond<LoginBeanRsp> register (LoginBeanReq req) {

        String deviceSN = req.getDeviceSN();
        String userId = req.getUserId();
        String passwd = req.getPasswd();

        /* ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("password", passwd));
        param.add(new BasicNameValuePair("userId", userId));
        param.add(new BasicNameValuePair("deviceSN", "DeviceSN01"));*/

        String xmlStr = String.format("" +
                "<ValidateUser xmlns=\"http://tempuri.org/\">\n" +
                "<UserID>%s</UserID>\n" +
                "<Pwd>%s</Pwd>\n" +
                "</ValidateUser>", userId, passwd);
        RetNode retNode = _webservice("ValidateUser", xmlStr, Timeout);

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String deviceToken = "123456";
        String result = "-1";
        String msg = "";

        try {
            String ret_body = StringUtils.regexXml(content, "ValidateUserResult");
            Log.d(TAG, "ret_body:"+ret_body);

            if (ret_body != null &&
                    ret_body.equals("true")) {
                result = "0";
            } else {
                result = "-1";
            }
        } catch (Exception e) {
        }

        Log.d(TAG, "result: "+result);
        /* if (res_arr.length >= 1) {
            deviceToken = res_arr[0];
        }*/
        /* if (res_arr.length >= 2) {
            result = res_arr[1];
        }*/
        /* if (res_arr.length >= 3) {
            msg = res_arr[2];
        }*/

        Respond<LoginBeanRsp> respond = new Respond<LoginBeanRsp>();
        respond.status = status;
        respond.message = msg;
        respond.ret = StringUtils.toInt(result);

        // deviceToken放置到sharePrefrence里
        if (respond.ret == 200) {
            ServerShare.getInstance().setToken(context, deviceToken);
            ServerShare.getInstance().setPasswd(context, passwd);
            ServerShare.getInstance().setUser(context, userId);
            ServerShare.getInstance().setDeviceSN(context, deviceSN);
        }

        LoginBeanRsp loginBeanRsp = new LoginBeanRsp();
        loginBeanRsp.setDeviceToken(deviceToken);

        // 保存登陆信息
        this.accessToken = deviceToken;

        respond.data = loginBeanRsp;
        return respond;
    }

    /**
     * 修改密码
     * @return
     */
    public int alterPwd (String userid, String oldPwd, String newPwd) {

        /* ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("password", passwd));
        param.add(new BasicNameValuePair("userId", userId));
        param.add(new BasicNameValuePair("deviceSN", "DeviceSN01"));*/

        String xmlStr = String.format("" +
                "<UserResetPwd xmlns=\"http://tempuri.org/\">\n" +
                "      <UserID>%s</UserID>\n" +
                "      <Old_Pwd>%s</Old_Pwd>\n" +
                "      <New_Pwd>%s</New_Pwd>\n" +
                "    </UserResetPwd>", userid, oldPwd, newPwd);
        RetNode retNode = _webservice("UserResetPwd", xmlStr, Timeout);

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String deviceToken = "123456";
        String result = "-1";
        String msg = "";

        try {
            String ret_body = StringUtils.regexXml(content, "ValidateUserResult");
            Log.d(TAG, "ret_body:"+ret_body);

            if (ret_body != null &&
                    ret_body.equals("true")) {
                result = "0";
            } else {
                result = "-1";
            }
        } catch (Exception e) {
        }

        Log.d(TAG, "result: "+result);
        /* if (res_arr.length >= 1) {
            deviceToken = res_arr[0];
        }*/
        /* if (res_arr.length >= 2) {
            result = res_arr[1];
        }*/
        /* if (res_arr.length >= 3) {
            msg = res_arr[2];
        }*/

        int ret = StringUtils.toInt(result);
        return ret;
    }

    /**
     * 获取Flash版本
     * @return
     */
    public Respond<CheckVersionFlashResp> checkVersionFlash (CheckVersionFlashReq req) {
        String serialNum = req.getSerialNum();

        String xmlStr = String.format("" +
                "<CheckVersion_Device xmlns=\"http://tempuri.org/\">\n" +
                "      <SerialNum>%s</SerialNum>\n" +
                "      <enum_DeviceType>USBPad</enum_DeviceType>\n" +
                "      <enum_VersionType>Firmware</enum_VersionType>\n" +
                "    </CheckVersion_Device>", serialNum);
        RetNode retNode = _webservice("CheckVersion_Device", xmlStr, Timeout);

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String result = "-1";
        String msg = "";

        CheckVersionFlashResp resp = null;

        try {
            String ret_body = StringUtils.regexXml(content, "CheckVersion_DeviceResult");
            Log.d(TAG, "ret_body:"+ret_body);

            if (ret_body != null) {
                // ret_body为json
                resp = new CheckVersionFlashResp();
                try {
                    JSONObject j_root = new JSONObject(ret_body);
                    String CurrentVersion = j_root.getString("CurrentVersion");
                    String LastVersion = j_root.getString("LastVersion");
                    boolean IsNeedUpdate = j_root.getBoolean("IsNeedUpdate");
                    // String FileUrl = j_root.getString("FileUrl");
                    String FileUrl = "http://119.23.219.80/orange/OGPDA_190927.x2";
                    String RealFileUrl = j_root.getString("RealFileUrl");
                    String FileName = j_root.getString("FileName");
                    IsNeedUpdate = true;

                    resp.setCurrentVersion(CurrentVersion);
                    resp.setLastVersion(LastVersion);
                    resp.setNeedUpdate(IsNeedUpdate);
                    resp.setFileUrl(FileUrl);
                    resp.setRealFileUrl(RealFileUrl);
                    resp.setFileName(FileName);
                    result = "0";
                } catch (Exception e) {
                }
            } else {
            }
        } catch (Exception e) {
        }

        Log.d(TAG, "result: "+result);

        Respond<CheckVersionFlashResp> respond = new Respond<>();
        respond.status = status;
        respond.message = msg;
        respond.ret = StringUtils.toInt(result);

        respond.data = resp;
        return respond;
    }

    /**
     * 获取Sensor版本
     * @return
     */
    public Respond<CheckVersionSensorResp> checkVersionSensor (CheckVersionSensorReq req) {
        String serialNum = req.getSerialNum();

        String xmlStr = String.format("" +
                "<CheckVersion_Sensor xmlns=\"http://tempuri.org/\">\n" +
                "      <SerialNum>%s</SerialNum>\n" +
                "    </CheckVersion_Sensor>", serialNum);
        RetNode retNode = _webservice("CheckVersion_Sensor", xmlStr, Timeout);

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String result = "-1";
        String msg = "";

        CheckVersionSensorResp resp = null;

        try {
            String ret_body = StringUtils.regexXml(content, "CheckVersion_SensorResult");
            Log.d(TAG, "ret_body:"+ret_body);

            if (ret_body != null) {
                // ret_body为json
                resp = new CheckVersionSensorResp();
                try {
                    JSONObject j_root = new JSONObject(ret_body);
                    String CurrentVersion = j_root.getString("CurrentVersion");
                    String LastVersion = j_root.getString("LastVersion");
                    boolean IsNeedUpdate = j_root.getBoolean("IsNeedUpdate");
                    // String FileUrl = j_root.getString("FileUrl");
                    String FileUrl = "http://119.23.219.80/orange/2C_V0.3.s19";
                    String RealFileUrl = j_root.getString("RealFileUrl");
                    String FileName = j_root.getString("FileName");

                    IsNeedUpdate = true;
                    resp.setCurrentVersion(CurrentVersion);
                    resp.setLastVersion(LastVersion);
                    resp.setNeedUpdate(IsNeedUpdate);
                    resp.setFileUrl(FileUrl);
                    resp.setRealFileUrl(RealFileUrl);
                    resp.setFileName(FileName);
                    result = "0";
                } catch (Exception e) {
                }
            } else {
            }
        } catch (Exception e) {
        }

        Log.d(TAG, "result: "+result);

        Respond<CheckVersionSensorResp> respond = new Respond<>();
        respond.status = status;
        respond.message = msg;
        respond.ret = StringUtils.toInt(result);

        respond.data = resp;
        return respond;
    }

    /**
     * 获取Excel文档
     * @return
     */
    public Respond<CheckVersionCoverageResp> checkVersionCoverage (CheckVersionCoverageReq req) {

        String serialNum = req.getSerialNum();

        /* ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("password", passwd));
        param.add(new BasicNameValuePair("userId", userId));
        param.add(new BasicNameValuePair("deviceSN", "DeviceSN01"));*/

        String xmlStr = String.format("" +
                "<CheckVersion_Coverage xmlns=\"http://tempuri.org/\">\n" +
                "      <SerialNum>%s</SerialNum>\n" +
                "    </CheckVersion_Coverage>", serialNum);
        RetNode retNode = _webservice("CheckVersion_Coverage", xmlStr, Timeout);

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String result = "-1";
        String msg = "";

        CheckVersionCoverageResp resp = null;

        try {
            String ret_body = StringUtils.regexXml(content, "CheckVersion_CoverageResult");
            Log.d(TAG, "ret_body:"+ret_body);

            if (ret_body != null) {
                // ret_body为json
                resp = new CheckVersionCoverageResp();
                try {
                    JSONObject j_root = new JSONObject(ret_body);
                    String CurrentVersion = j_root.getString("CurrentVersion");
                    String LastVersion = j_root.getString("LastVersion");
                    boolean IsNeedUpdate = j_root.getBoolean("IsNeedUpdate");
                    // String FileUrl = j_root.getString("FileUrl");
                    String FileUrl = "http://119.23.219.80/orange/MMY_20190814.xls";
                    String RealFileUrl = j_root.getString("RealFileUrl");
                    String FileName = j_root.getString("FileName");

                    IsNeedUpdate = true;
                    resp.setCurrentVersion(CurrentVersion);
                    resp.setLastVersion(LastVersion);
                    resp.setNeedUpdate(IsNeedUpdate);
                    resp.setFileUrl(FileUrl);
                    resp.setRealFileUrl(RealFileUrl);
                    resp.setFileName(FileName);
                    result = "0";
                } catch (Exception e) {
                }
            } else {
            }
        } catch (Exception e) {
        }

        Log.d(TAG, "result: "+result);

        Respond<CheckVersionCoverageResp> respond = new Respond<>();
        respond.status = status;
        respond.message = msg;
        respond.ret = StringUtils.toInt(result);

        respond.data = resp;
        return respond;
    }

    /**
     * 注册新账号 (同步)
     * @return
     */
    public Respond<RegisterBeanRsp> registerNewAccount (RegisterBeanReq req) {

        String address = req.getAddress();
        String username = req.getUserName();
        String city = req.getCity();
        String contactName = req.getContactName();
        String country = req.getCountry();
        String email = req.getEmail();
        String telephoneNumber = req.getTelephoneNumber();
        String officeTelephoneNumber = req.getOfficeTelephoneNumber();
        String state = req.getState();
        String userTitle = req.getUserTitle();
        String password = req.getPassword();
        String macAddress = StringUtils.getMacDefault(context);

        String xmlStr = String.format("" +
                "<Register xmlns=\"http://tempuri.org/\">\n" +
                "      <Reg_UserInfo>\n" +
                "        <UserID>%s</UserID>\n" +
                "        <UserPwd>%s</UserPwd>\n" +
                "      </Reg_UserInfo>\n" +
                "      <Reg_StoreInfo>\n" +
                "        <StoreType>Distributor</StoreType>\n" +
                "        <CompName>%s</CompName>\n" +
                "        <FirstName>%s</FirstName>\n" +
                "        <LastName>%s</LastName>\n" +
                "        <Contact_Tel>%s</Contact_Tel>\n" +
                "        <Continent>%s</Continent>\n" +
                "        <Country>%s</Country>\n" +
                "        <State>%s</State>\n" +
                "        <City>%s</City>\n" +
                "        <Street>%s</Street>\n" +
                "        <CompTel>%s</CompTel>\n" +
                "      </Reg_StoreInfo>\n" +
                "      <Reg_DeviceInfo>\n" +
                "        <SerialNum>%s</SerialNum>\n" +
                "        <DeviceType>OGenius</DeviceType>\n" +
                "        <ModelNum>%s</ModelNum>\n" +
                "        <AreaNo>%s</AreaNo>\n" +
                "        <Firmware_1_Copy>%s</Firmware_1_Copy>\n" +
                "        <Firmware_2_Copy>%s</Firmware_2_Copy>\n" +
                "        <Firmware_3_Copy>%s</Firmware_3_Copy>\n" +
                "        <DB_Copy>%s</DB_Copy>\n" +
                "        <MacAddress>%s</MacAddress>\n" +
                "        <IpAddress>%s</IpAddress>\n" +
                "      </Reg_DeviceInfo>\n" +
                "    </Register>",
                username/*userId*/, password/*userpwd*/, "Orange"/*compName*/, contactName/*FirstName*/,
                userTitle/*LastName*/,telephoneNumber/*Contact_Tel*/,"Asia"/*Continent*/,country/*Country*/,state/*State*/,city/*City*/,
                address/*Street*/,officeTelephoneNumber/*CompTel*/,"**"/*SerialNum*/,"**"/*ModelNum*/,"**"/*AreaNo*/,"**"/*Firmware_1_Copy*/,
                "**"/*Firmware_2_Copy*/,"**"/*Firmware_3_Copy*/,"**"/*DB_Copy*/,macAddress/*MacAddress*/,"**"/*IpAddress*/
                );
        RetNode retNode = _webservice("Register", xmlStr, Timeout);

        /* ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("userName", username));
        param.add(new BasicNameValuePair("userTitle", userTitle));
        param.add(new BasicNameValuePair("contactName", contactName));
        param.add(new BasicNameValuePair("telephoneNumber", telephoneNumber));
        param.add(new BasicNameValuePair("officeTelephoneNumber", officeTelephoneNumber));
        param.add(new BasicNameValuePair("email", email));
        param.add(new BasicNameValuePair("address", address));
        param.add(new BasicNameValuePair("city", city));
        param.add(new BasicNameValuePair("state", state));
        param.add(new BasicNameValuePair("country", country));
        param.add(new BasicNameValuePair("password", password));

        RetNode retNode = _post("/api/admin/user/register", param, Timeout, null);*/

        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;
        String ret_body = StringUtils.regexXml(content, "RegisterResult");

        // String[] res_arr = content.split(Server.SPLID_WORD);
        String deviceToken = "";
        String result = "0";
        String msg = "";

        /*if (res_arr.length >= 1) {
            deviceToken = res_arr[0];
        }
        if (res_arr.length >= 2) {
            result = res_arr[1];
        }
        if (res_arr.length >= 3) {
            msg = res_arr[2];
        }*/

        if (ret_body != null &&
                ret_body.equals("true")) {
            result = "0";
        } else {
            result = "-1";
        }

        Respond<RegisterBeanRsp> respond = new Respond<RegisterBeanRsp>();
        respond.status = status;
        respond.message = msg;
        respond.ret = StringUtils.toInt(result);

        RegisterBeanRsp registerBeanRsp = new RegisterBeanRsp();
        respond.data = registerBeanRsp;
        return respond;
    }

    /**
     * 异步请求
     */
    public void getLastestVersion (LastestVersionReq req, CB<Respond<LastestVersionRsp>> rsp_cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Respond<LastestVersionRsp> rsp = getLastestVersion(req);
                rsp_cb.onRsp(rsp);
            }
        }).start();
    }

    /**
     * 获取最新版本
     * @return
     */
    public Respond<LastestVersionRsp> getLastestVersion (LastestVersionReq req) {

        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        ArrayList<NameValuePair> headerParam = new ArrayList<NameValuePair>();
        headerParam.add(new BasicNameValuePair("authorization", this.accessToken));

        RetNode retNode = _get("/api/admin/device/latest-version", param, Timeout,
                headerParam);
        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;
        String[] res_arr = content.split(Server.SPLID_WORD);

        String lastFirmwareVersion = "";
        String AMcuFirmwareVersion = "";
        String BMcuFirmwareVersion = "";
        String CMcuFirmwareVersion = "";
        String DMcuFirmwareVersion = "";
        String lastSensorModelDataVersion = "";
        String result = "0";
        String message = "";

        if (res_arr.length >= 1) {
            lastFirmwareVersion = res_arr[0];
        }
        if (res_arr.length >= 2) {
            AMcuFirmwareVersion = res_arr[1];
        }
        if (res_arr.length >= 3) {
            BMcuFirmwareVersion = res_arr[2];
        }
        if (res_arr.length >= 4) {
            CMcuFirmwareVersion = res_arr[3];
        }
        if (res_arr.length >= 5) {
            DMcuFirmwareVersion = res_arr[4];
        }
        if (res_arr.length >= 6) {
            lastSensorModelDataVersion = res_arr[5];
        }
        if (res_arr.length >= 7) {
            result = res_arr[6];
        }
        if (res_arr.length >= 8) {
            message = res_arr[7];
        }

        LastestVersionRsp lastestVersionRsp = new LastestVersionRsp();
        lastestVersionRsp.setAVersion(AMcuFirmwareVersion);
        lastestVersionRsp.setBVersion(BMcuFirmwareVersion);
        lastestVersionRsp.setCVersion(CMcuFirmwareVersion);
        lastestVersionRsp.setDVersion(DMcuFirmwareVersion);
        lastestVersionRsp.setMainVersion(lastFirmwareVersion);
        lastestVersionRsp.setSensorVersion(lastSensorModelDataVersion);

        // 构造
        this.lastestVersionRsp = lastestVersionRsp;

        Respond<LastestVersionRsp> respond = new Respond<LastestVersionRsp>();
        respond.status = status;
        respond.message = message;
        respond.ret = StringUtils.toInt(result);
        respond.data = lastestVersionRsp;
        return respond;
    }

    /**
     * 异步请求
     */
    public void getAllMcuUpdateUrl (GetAllMcuUpdateUrlBeanReq req, CB<Respond<GetAllMcuUpdateUrlBeanRsp>> rsp_cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Respond<GetAllMcuUpdateUrlBeanRsp> rsp = getAllMcuUpdateUrl(req);
                rsp_cb.onRsp(rsp);
            }
        }).start();
    }

    /**
     * 获取所有Mcu的升级链接
     * @return
     * @deprecated 不可用
     */
    public Respond<GetAllMcuUpdateUrlBeanRsp> getAllMcuUpdateUrl (GetAllMcuUpdateUrlBeanReq req) {

        GetAllMcuUpdateUrlBeanRsp rsp = new GetAllMcuUpdateUrlBeanRsp ();

        int status = 200;
        boolean if_success = true;

        // 第一颗
        GetMcuUpdateUrlBeanReq mcuReq = new GetMcuUpdateUrlBeanReq();
        mcuReq.setIndex(1);
        Respond<GetMcuUpdateUrlBeanRsp> mcuRsp = getMcuUpdateUrl(mcuReq);
        rsp.setAUrl(mcuRsp.getData().getUrl());
        if_success = (mcuRsp.status==200) && if_success;

        // 第二颗
        mcuReq.setIndex(2);
        mcuRsp = getMcuUpdateUrl(mcuReq);
        rsp.setBUrl(mcuRsp.getData().getUrl());
        if_success = (mcuRsp.status==200) && if_success;

        // 第三颗
        mcuReq.setIndex(3);
        mcuRsp = getMcuUpdateUrl(mcuReq);
        rsp.setCUrl(mcuRsp.getData().getUrl());
        if_success = (mcuRsp.status==200) && if_success;

        // 第四颗
        mcuReq.setIndex(4);
        mcuRsp = getMcuUpdateUrl(mcuReq);
        rsp.setDUrl(mcuRsp.getData().getUrl());
        if_success = (mcuRsp.status==200) && if_success;

        Respond<GetAllMcuUpdateUrlBeanRsp> respond = new Respond<GetAllMcuUpdateUrlBeanRsp>();
        respond.status = (if_success==true)?200:500;
        respond.message = "";
        respond.ret = (respond.status==200)?1:0;
        respond.data = rsp;
        return respond;
    }

    /**
     * 获取单个Mcu的升级链接
     * @return
     * @deprecated 不可用
     */
    public Respond<GetMcuUpdateUrlBeanRsp> getMcuUpdateUrl (GetMcuUpdateUrlBeanReq req) {
        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        ArrayList<NameValuePair> headerParam = new ArrayList<NameValuePair>();
        headerParam.add(new BasicNameValuePair("authorization", this.accessToken));

        LastestVersionRsp lastestVersionRsp = this.lastestVersionRsp;
        String mcuVersion = "";
        int index = req.getIndex();

        switch (index) {
            case 1: {
                mcuVersion = lastestVersionRsp.getAVersion();
            } break;
            case 2: {
                mcuVersion = lastestVersionRsp.getBVersion();
            } break;
            case 3: {
                mcuVersion = lastestVersionRsp.getCVersion();
            } break;
            default: {
                mcuVersion = lastestVersionRsp.getDVersion();
            } break;
        }
        RetNode retNode = _get(String.format("/api/admin/device/firmware/%s/%s",
                lastestVersionRsp.getMainVersion(), mcuVersion), param, Timeout,
                headerParam);
        int status = retNode.status;

        // 设置返回值
        String content = retNode.data;
        String[] res_arr = content.split(Server.SPLID_WORD);

        String url = "";
        String result = "0";
        String message = "";

        if (res_arr.length >= 1) {
            url = res_arr[0];
        }
        if (res_arr.length >= 2) {
            result = res_arr[1];
        }
        if (res_arr.length >= 3) {
            message = res_arr[2];
        }

        GetMcuUpdateUrlBeanRsp getMcuUpdateUrlBeanRsp = new GetMcuUpdateUrlBeanRsp();
        getMcuUpdateUrlBeanRsp.setUrl(url);

        Respond<GetMcuUpdateUrlBeanRsp> respond = new Respond<GetMcuUpdateUrlBeanRsp>();
        respond.status = status;
        respond.message = message;
        respond.ret = StringUtils.toInt(result);
        respond.data = getMcuUpdateUrlBeanRsp;
        return respond;
    }

    /**
     * 上报奔溃日志
     * @return
     */
    public Respond<SensorLogReportBeanRsp> sensorLogReport (SensorLogReportBeanReq req) {

        return null;
    }

    /**
     * 获取App最新版本信息
     * @return
     */
    public Respond<GetSoftwareUrlBeanRsp> getSoftwareUpdateUrl (GetSoftwareUrlBeanReq req) {

        return null;
    }

    /**
     * 获取传感器资料库
     * @return
     */
    public Respond<GetSensorUpdateUrlBeanRsp> getSensorUpdateUrl (GetSensorUpdateUrlBeanReq req) {

        return null;
    }

    /**
     * 更新传感器信息
     * @return
     */
    public Respond<ReportSensorInfoBeanRsp> reportSensorInfo (ReportSensorInfoBeanReq req) {

        return null;
    }

    public static class RetNode {
        public int status;
        public String data;

        public RetNode () {
            status = -1;
            data = null;
        }
    }

    /**
     * 响应码
     * @param <T>
     */
    public static class Respond<T> {

        public final static String NoAccessRight = "NoAccessRight";      // 沒有足夠的權限進行存取
        public final static String AuthorizationExpired = "AuthorizationExpired";           // AuthorizationExpired=經銷商授權過期

        private T data;         // 数据放置此
        private int ret;        // 1-成功, 0-失败
        private int status;     // Http请求码
        private String errorCode;       // 错误说明
        private String message;     // 消息说明

        public Respond() {}

        public Respond(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public int getStatus () {
            return status;
        }

        public int getRet () {
            return ret;
        }

        public String getErrorCode () {
            return errorCode;
        }

        public String getMessage () {
            return message;
        }
    }

    // 异步响应回调
    public static interface CB<T> {
        public void onRsp (T rsp);
    }

    // 错误异步回调
    public static interface ErrorCB {
        public void onTokenInvalid (RetNode retNode);      // token失效
        public void onTimeout (RetNode retNode);           // 请求超时
        public void onOther (RetNode retNode);             // 其他错误
    }
}
