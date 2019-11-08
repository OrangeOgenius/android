package com.orange.tpms.lib.test;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.orange.tpms.app.TPMSApp;
import com.orange.tpms.bean.CheckVersionCoverageReq;
import com.orange.tpms.bean.CheckVersionCoverageResp;
import com.orange.tpms.bean.CheckVersionFlashReq;
import com.orange.tpms.bean.CheckVersionFlashResp;
import com.orange.tpms.bean.CheckVersionSensorReq;
import com.orange.tpms.bean.CheckVersionSensorResp;
import com.orange.tpms.lib.api.Downloader;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.api.Server;
import com.orange.tpms.lib.db.share.SettingShare;
import com.orange.tpms.lib.db.tb.MMyAllTable;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.utils.FileUtil;
import com.orange.tpms.utils.HttpDownloader;

import java.io.File;
import java.util.ArrayList;

import bean.hardware.SensorDataBean;
import bean.mmy.MMyBean;
import bean.server.rep.GetAllMcuUpdateUrlBeanRsp;
import bean.server.rep.RegisterBeanRsp;
import bean.server.req.GetAllMcuUpdateUrlBeanReq;
import bean.server.req.LastestVersionReq;
import bean.server.req.LoginBeanReq;
import bean.server.req.RegisterBeanReq;

/**
 * 测试接口
 * Created by john on 2019/3/27.
 */
public class MTest {
    public static String TAG = MTest.class.getName();
    Context context;
    TPMSApp app;

    public MTest () {}

    public void setContext (Context context) {
        this.context = context;
    }

    public void setTPMS (TPMSApp app) {
        this.app = app;
    }

    /**
     * 获取MMY
     */
    private void printMMy () {
        /* String make = MMy.getMakeWithLogoFileName("logo_ABARTH");
        Log.d(TAG, "printMMy: "+make);
        // SettingShare.setTemperatureUnit(context, SettingShare.TemperatureUnitEnum.F);
        SettingShare.Unit unit = SettingShare.getUnit(context);

        Log.d(TAG, String.format("unit:%s,%s,%s", unit.temperatureUnit.ordinal(),
                unit.tirePressureUnit.ordinal(), unit.numeralSystemUnit.ordinal()));*/

        // MMy.LoadAssetsExcel (context);

        /* MMy.LoadMMy(context, null);
        ArrayList<MMyBean> arrayList = MMyAllTable.ReadAllMMy(context);
        for (int i=0;i<arrayList.size();i++) {
            MMyBean mMyBean = arrayList.get(i);
            MMy.Print(mMyBean);
        }*/
        // MMy.visit(context, "934");
        /* ArrayList<MMyBean> arrayList = MMy.getMMyWithPrdNum(context, "BEA017");
        for (int i=0;i<arrayList.size();i++) {
            MMyBean mMyBean = arrayList.get(i);
            MMy.Print(mMyBean);
        }*/
    }

    private void printLastestVersion (Server server) {
        LastestVersionReq lastestVersionReq = new LastestVersionReq();
        server.getLastestVersion(lastestVersionReq);
    }

    /**
     * 获取所有mcu的更新链接
     */
    private void printGetAllMcuUrl (Server server) {
        GetAllMcuUpdateUrlBeanReq req = new GetAllMcuUpdateUrlBeanReq();
        server.getAllMcuUpdateUrl(req, new Server.CB<Server.Respond<GetAllMcuUpdateUrlBeanRsp>>() {
            @Override
            public void onRsp(Server.Respond<GetAllMcuUpdateUrlBeanRsp> respond) {
                int status = respond.getStatus();
                String message = respond.getMessage();
                String aUrl = respond.getData().getAUrl();
                String bUrl = respond.getData().getBUrl();
                String cUrl = respond.getData().getCUrl();
                String dUrl = respond.getData().getDUrl();

                Log.d(TAG, "aUrl: "+aUrl);
                Log.d(TAG, "bUrl: "+bUrl);
                Log.d(TAG, "dUrl: "+cUrl);
                Log.d(TAG, "dUrl: "+dUrl);
            }
        });
    }

    /**
     * 检测是否烧录完成
     */
    private void programCheck () {
        SensorHandler sensorHandler = HardwareApp.getInstance().sensorHandler;
        sensorHandler.programCheck((ret,data) -> {
            // 确认烧录完成
        });
    }

    private void writeMulti () {
        SensorHandler sensorHandler = HardwareApp.getInstance().sensorHandler;
        sensorHandler.writeSensorFirmwareWithProgress(2, new SensorHandler.FlashWriteProgress() {
            @Override
            public void start(int total) {

            }

            @Override
            public void progress(int progress, int total) {

            }

            @Override
            public void finish(int total) {

            }

            @Override
            public void fail(int errcode, int start, int total) {

            }
        },"");
    }

    /**
     * 登陆接口
     */
    private void printLogin (Server server) {
        LoginBeanReq loginBeanReq = new LoginBeanReq();
        loginBeanReq.setPasswd("0000");
        loginBeanReq.setDeviceSN("123");
        loginBeanReq.setUserId("admin");
        server.register(loginBeanReq);
    }

    private void printRegister (Server server) {
        RegisterBeanReq registerBeanReq = new RegisterBeanReq();
        registerBeanReq.setAddress("深圳市福田区");
        registerBeanReq.setCity("深圳市");
        registerBeanReq.setContactName("2094");
        registerBeanReq.setCountry("中国");
        registerBeanReq.setEmail("97823409022@qq.com");
        registerBeanReq.setTelephoneNumber("234323");
        registerBeanReq.setOfficeTelephoneNumber("2394069022");
        registerBeanReq.setPassword("123456");
        registerBeanReq.setState("广东省");
        registerBeanReq.setUserName("john");
        registerBeanReq.setUserTitle("技术员");
        Server.Respond<RegisterBeanRsp> respond = server.registerNewAccount(registerBeanReq);
        int status = respond.getStatus();
        String message = respond.getMessage();
        Log.d(TAG, "onRsp: "+status+","+message);
    }

    private int alterPwd (Server server) {
        return server.alterPwd("john", "123456", "");
    }

    private void printVersion (Server server) {
        /* String dir = Environment.getExternalStorageDirectory() + "/";
        File file = new File(dir+"resource");
        file.mkdir();*/

        // 下载MMY Excel
        /* new Downloader().loadCoverage(server, "99", new FileUtil.FilePrograss() {
            @Override
            public void progress(int total, int progress) {
                Log.d(TAG, String.format("total: %s, progress: %s", total,progress));
            }

            @Override
            public void finish(int total) {
                Log.d(TAG, String.format("total: %s, finish", total));
            }

            @Override
            public void start(int total) {
                Log.d(TAG, String.format("start: %s", total));
            }

            @Override
            public void fail(String msg) {
                Log.d(TAG, msg);
            }
        }); */
        /* new Downloader().loadSensor(server, "99" , new FileUtil.FilePrograss() {
            @Override
            public void progress(int total, int progress) {
                Log.d(TAG, String.format("loadSensor -> total: %s, progress: %s", total,progress));
            }

            @Override
            public void finish(int total) {
                Log.d(TAG, String.format("total: %s, finish", total));
            }

            @Override
            public void start(int total) {
                Log.d(TAG, String.format("start: %s", total));
            }

            @Override
            public void fail(String msg) {
                Log.d(TAG, msg);
            }
        });*/
        // 下载文件并显示进度
        new Downloader().loadFlash(server, "99" , new FileUtil.FilePrograss() {
            @Override
            public void progress(int total, int progress) {
                Log.d(TAG, String.format("loadSensor -> total: %s, progress: %s", total,progress));
            }

            @Override
            public void finish(int total) {
                Log.d(TAG, String.format("total: %s, finish", total));
            }

            @Override
            public void start(int total) {
                Log.d(TAG, String.format("start: %s", total));
            }

            @Override
            public void fail(String msg) {
                Log.d(TAG, msg);
            }
        });

        /*CheckVersionSensorReq checkVersionSensorReq = new CheckVersionSensorReq();
        checkVersionSensorReq.setSerialNum("99");
        Server.Respond<CheckVersionSensorResp> respond_sensor = server.checkVersionSensor(checkVersionSensorReq);
        int status = respond_sensor.getStatus();
        CheckVersionSensorResp resp_sensor = respond_sensor.getData();
        String fileUrl = resp_sensor.getFileUrl();
        Log.d(TAG, "onRsp sensor: "+status+","+fileUrl);*/

        /*CheckVersionFlashReq checkVersionFlashReq = new CheckVersionFlashReq();
        checkVersionFlashReq.setSerialNum("99");
        Server.Respond<CheckVersionFlashResp> respond_flash = server.checkVersionFlash(checkVersionFlashReq);
        status = respond_flash.getStatus();
        CheckVersionFlashResp resp_flash = respond_flash.getData();
        fileUrl = resp_flash.getFileUrl();
        Log.d(TAG, "onRsp flash: "+status+","+fileUrl);*/

        /* CheckVersionCoverageReq checkVersionCoverageReq = new CheckVersionCoverageReq();
        checkVersionCoverageReq.setSerialNum("99");
        Server.Respond<CheckVersionCoverageResp> respond_sensor = server.checkVersionCoverage(checkVersionCoverageReq);
        int status = respond_sensor.getStatus();
        CheckVersionCoverageResp resp_sensor = respond_sensor.getData();
        String fileUrl = resp_sensor.getFileUrl();
        Log.d(TAG, "onRsp sensor: "+status+","+fileUrl);*/
    }

    public void run () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                printVersion (new Server(context));
                // printVersion(new Server(context));
                // writeMulti ();
                /*Server server = new Server(context);
                printLogin(server);*/
                // printRegister(server);
                // printLastestVersion (server);
                // printGetAllMcuUrl (server);
                // printMMy();
                // printLogin (new Server(context));
                // printRegister (new Server(context));
                // printVersion (new Server(context));
                /* try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                programCheck ();*/
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // ZoneTable.initData(context);        // 初始化城市列表
                /*ArrayList<CityBean> arrayList = ZoneTable.getContinent(context);        // 洲列表

                for (int i=0;i<arrayList.size();i++) {
                    ZoneTable.Print(arrayList.get(i));
                }
                arrayList = ZoneTable.getCountry(context, "1"); // 亚洲

                for (int i=0;i<arrayList.size();i++) {
                    ZoneTable.Print(arrayList.get(i));
                }
                arrayList = ZoneTable.getCity(context, "265");      // 广东省

                for (int i=0;i<arrayList.size();i++) {
                    ZoneTable.Print(arrayList.get(i));
                }*/
                /*SettingShare.setAutoLockTimeout(context, SettingShare.TimeOutUnitEnum.MIN_5);
                SettingShare.TimeOutUnitEnum timeOutUnitEnum = SettingShare.getAutoLockTimeout(context);*/
                // Log.d(TAG, "run: "+timeout_original);
                // HardTest.Test(app.hardwareApp);
            }
        }).start();
    }

    /**
     * 硬件测试
     */
    public static void HardwareTest () {
        // 扫描硬件码
        HardwareApp.openScan();
    }
}
