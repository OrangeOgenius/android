package com.orange.tpms.lib.hardware;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.zyapi.CommonApi;
import com.orange.tpms.Callback.Scan_C;
import com.orange.tpms.R;
import com.orange.tpms.utils.OgCommand;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by john on 2019/6/22.
 */
public class HardwareApp extends BaseHardware {

    public static String TAG = HardwareApp.class.getName();

    private static int mComFd = -1;
    static CommonApi mCommonApi;

    static HardwareApp instance = null;

    public static boolean isCanSend = true;
    public static Scan_C scan_c=null;
    private final int MAX_RECV_BUF_SIZE = 1024;
    private boolean isOpen = false;
    private MediaPlayer player;
    private final static int SHOW_RECV_DATA = 1;
    private byte[] recv;
    private String strRead;
    public static boolean isScanDomn = false;

    public static StringBuffer sb1 = new StringBuffer();

    static Handler handler1 = new Handler();

    byte[] serial_byte;

    // SCAN按键监听
    private ScanBroadcastReceiver scanBroadcastReceiver;

    // 扫描消息到达广播
    private ScanMsgBroadcastReceiver scanMsgBroadcastReceiver;

    // uart2消息到达广播
    private Uart2MsgBroadcastReceiver uart2MsgBroadcastReceiver;

    Handler h;

    public ArrayList<DataReceiver> iDataReceiver_list = null;
    public DataReceiver iDataReceiver;

    //是否开启硬件功能
    private boolean enableHareware = true;

    public static HardwareApp getInstance() {
        if (instance == null) {
            instance = new HardwareApp();
        }
        return instance;
    }

    public HardwareApp () {
        super ();
        instance = this;
        iDataReceiver_list = new ArrayList<DataReceiver>();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // 初始
        init();
        // 实例化MediaPlayer
        player = MediaPlayer.create(this.app.getApplicationContext(), R.raw.beep);
    }

    /**
     * 设置Application
     */
    public HardwareApp setApplication (Application app) {
        this.app = app;
        return instance;
    }

    /**
     * 是否开启硬件功能
     */
    public HardwareApp setEnableHareware (boolean enableHareware) {
        this.enableHareware = enableHareware;
        return instance;
    }

    /**
     * 是否开启硬件功能
     */
    public boolean isEnableHareware () {
        return enableHareware;
    }

    @SuppressLint("HandlerLeak")
    private void init() {
        if(enableHareware){
            mCommonApi = new CommonApi();
        }
        initGPIO();

        new Handler().postDelayed(() -> {
            if (mComFd > 0) {
                open();
                isOpen = true;
                readData();
            } else {
                isOpen = false;
            }
        }, 2000);

        // 利用Handler更新UI
        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    if (msg.obj != null) {
                        String str = "" + msg.obj;
                        if (!str.trim().contains("##56")) {
                            if (!str.trim().contains("##55")) {
                                if (!str.trim().equals("start")) {
                                    if (!str.trim().contains("start-V404")) {

                                        if (str.contains("uart4_")) {

                                            String[] str1 = str.split("\\$");

                                            if (str1.length > 1) {

                                                String str_uart4="";

                                                str_uart4=bytesToHexString(recv);

                                                String[] str2=str_uart4.split("24 ");

                                                str_uart4="";

                                                if (str2.length > 1) {
                                                    for (int i = 1; i < str2.length; i++) {
                                                        if(str_uart4.length()<=0){
                                                            str_uart4=str_uart4+str2[i];
                                                        }else{
                                                            str_uart4=str_uart4+"24 "+str2[i];
                                                        }
                                                    }
                                                }


                                                Intent intentBroadcast = new Intent();
                                                intentBroadcast
                                                        .setAction("com.qs.uart4code");
                                                intentBroadcast.putExtra(
                                                        "code", str_uart4);
                                                app.sendBroadcast(intentBroadcast);
                                            }
                                        } else if (str.contains("uart2_")) {

                                            String[] str1 = str.split("\\$");

                                            String str_uart2="";

                                            if (str1.length > 1) {

                                                str_uart2=bytesToHexString(recv);

                                                String[] str2=str_uart2.split("24 ");

                                                str_uart2="";

                                                if (str2.length > 1) {
                                                    for (int i = 1; i < str2.length; i++) {
                                                        if(str_uart2.length()<=0){
                                                            str_uart2=str_uart2+str2[i];
                                                        }else{
                                                            str_uart2=str_uart2+"24 "+str2[i];
                                                        }

                                                    }
                                                }


                                                Intent intentBroadcast = new Intent();
                                                intentBroadcast
                                                        .setAction("com.qs.uart2code");
                                                intentBroadcast.putExtra(
                                                        "code",str_uart2);
                                                app.sendBroadcast(intentBroadcast);
                                            }

                                        } else if (str.contains("scan_")) {

                                            String[] str1 = str.split("\\$");

                                            String str_scan="";

                                            if (str1.length > 1) {

                                                for (int i = 1; i < str1.length; i++) {
                                                    if(str_scan.length()<=0){
                                                        str_scan=str_scan+str1[i];
                                                    }else{
                                                        str_scan=str_scan+"$"+str1[i];
                                                    }
                                                }

                                                // player.start();

                                                Intent intentBroadcast = new Intent();
                                                intentBroadcast
                                                        .setAction("com.qs.scancode");
                                                intentBroadcast.putExtra(
                                                        "code", str_scan);
                                                app.sendBroadcast(intentBroadcast);
                                            }

                                        }

                                        sb1.setLength(0);

                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        // 监听广播
        initReceiver ();


        // 初始化配置
    }

    boolean ifInitCb = false;
    public void initWithCb (Activity activity, InitCb cb) {
        ifInitCb = false;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!ifInitCb) {
//                    // 超时重新执行一次
//                    Log.d(TAG, "initWithCb: timeout");
//                    cb.pingReceive(-3);     // 超时重试
//                    initWithCb(activity, cb);
//                }
//            }
//        }, 3000);       // 3s超时
        if(enableHareware){

            try {
                this.open5V(false);
                Thread.sleep(500);
                this.setGpio1V(false);
                Thread.sleep(500);
                this.open9V(false);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.switchScan(true);
            this.openPB5(true);     // 开启PB5
            new Thread(() ->{
                this.setGpio1V(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.open5V(true);
                cb.onStart();
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(() -> this.open9V(true));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }).start();
        }else{
            //直接跳过判断
            cb.pingReceive(0);
        }
    }

    public interface InitCb {
        public void onStart ();
        public void pingReceive (int ret);
    }

    /**
     * 监听广播
     */
    private void initReceiver () {
        /** 监听扫描消息广播 */
        this.scanMsgBroadcastReceiver = new ScanMsgBroadcastReceiver (this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qs.scancode");
        this.app.registerReceiver(this.scanMsgBroadcastReceiver, intentFilter);

        /** 监听UART2串口消息广播 */
        uart2MsgBroadcastReceiver = new Uart2MsgBroadcastReceiver(this);
        IntentFilter intentFilter_uart2 = new IntentFilter();
        intentFilter_uart2.addAction("com.qs.uart2code");
        this.app.registerReceiver(uart2MsgBroadcastReceiver, intentFilter_uart2);
    }

    /**
     * 解除监听广播
     */
    public void unRegisterBrocast(){
        this.app.unregisterReceiver(scanMsgBroadcastReceiver);
        this.app.unregisterReceiver(uart2MsgBroadcastReceiver);
    }

    private boolean ifEnableScan = false;
    /**
     * 是否使能扫描功能
     */
    public void switchScan (boolean ifEnable) {
        ifEnableScan = ifEnable;
        if (ifEnable) {
            if (scanBroadcastReceiver == null) {
                scanBroadcastReceiver = new ScanBroadcastReceiver(this);
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("ismart.intent.scandown");
            app.registerReceiver(scanBroadcastReceiver, intentFilter);
        } else {
            if (scanBroadcastReceiver != null) {
                app.unregisterReceiver(scanBroadcastReceiver);
            }
        }
    }

    /**
     * 设置GPIO1电压状态
     */
    public void setGpio1V (boolean ifOpen) {
        if (ifOpen) {
            // 打开
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x31,0x4F,0x4F,0x4E});
        } else {
            // 关闭
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x31,0x4F,0x4F,0x46});
        }
    }

    /**
     * 设置GPIO1电压状态
     */
    public void open5V (boolean ifOpen) {

        Log.d(TAG, "open5V: "+ifOpen);
        if (ifOpen) {
            // 打开
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x35,0x56,0x4F,0x4E});
        } else {
            // 关闭
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x35,0x56,0x4F,0x46});
        }
    }

    /**
     * 设置GPIO1电压状态
     */
    public void open9V (boolean ifOpen) {
        Log.d(TAG, "open9V: "+ifOpen);
        if (ifOpen) {
            // 打开
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x39,0x56,0x4F,0x4E});
        } else {
            // 关闭
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x39,0x56,0x4F,0x46});
        }
    }

    /**
     * 打开PB5
     */
    public void openPB5 (boolean ifOpen) {
        Log.d(TAG, "openPB5: "+ifOpen);
        if (ifOpen) {
            // 打开
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x35,0x39,0x4F,0x4E});
        } else {
            // 关闭
            HardwareApp.send(new byte[]{0x1B,0x23,0x23,0x35,0x39,0x4F,0x46});
        }
    }

    /**
     * 开始扫码
     */
    public void scan(){
        // 发送扫描指令
        HardwareApp.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x35, 0x44, 0x4E });

        // 开始扫码
        beginScan ();
    }

    /**
     * 接收消息, 包括扫描消息, 硬件UART消息
     */
    public void addDataReceiver (DataReceiver dataReceiver) {
        iDataReceiver_list.add(dataReceiver);
    }

    /**
     * 删除消息, 包括扫描消息, 硬件UART消息
     */
    public void removeDataReceiver (DataReceiver dataReceiver) {
        iDataReceiver_list.remove(dataReceiver);
    }

    public void setMsgDataReceiver (DataReceiver dataReceiver) {
        this.iDataReceiver = dataReceiver;
    }

    public static interface DataReceiver {
        public void scanReceive ();
        public void scanMsgReceive (String content);
        public void uart2MsgReceive (String content);
    }

    /**
     * 读数据线程
     */
    private void readData() {
        if(mCommonApi == null){
            return;
        }
        new Thread() {
            public void run() {
                while (isOpen) {
                    int ret = 0;
                    byte[] buf = new byte[MAX_RECV_BUF_SIZE + 1];
                    ret = mCommonApi.readComEx(mComFd, buf, MAX_RECV_BUF_SIZE,
                            0, 0);
                    if (ret <= 0) {
                        Log.d("", "read failed!!!! ret:" + ret);
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        continue;
                    } else {
                        // Log.e("", "1read success:");
                    }
                    recv = new byte[ret];
                    System.arraycopy(buf, 0, recv, 0, ret);

                    try {
                        strRead = new String(recv, "UTF-8");

                        Log.e("RX:", "22数据："+strRead);
                        if(strRead.contains("uart2_$")){
                            String ss=bytesToHexString(recv);
                            OgCommand.Rx= OgCommand.Rx+ss.replace("75 61 72 74 32 5f 24","").replace(" ","").toUpperCase();
                            Log.e("DATA:", "RX："+ss.replace("75 61 72 74 32 5f 24",""));
                            Log.d(TAG, "receiver: " + ss.replace("75 61 72 74 32 5f 24",""));
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (strRead.contains("scan_$")) {
                        Message msg = handler.obtainMessage(SHOW_RECV_DATA);
                        msg.obj = strRead;
                        msg.sendToTarget();
                    }
                }
            }
        }.start();
    }

    boolean iscanScan = false;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_RECV_DATA:
                    String barCodeStr1 = (String) msg.obj;
                    Log.e("", "1read success:" + barCodeStr1);
                    if (barCodeStr1.trim() != "") {
                        if (isOpen) {
                            // if (!barCodeStr1.trim().contains("")) {
                                if (!barCodeStr1.trim().contains("##55")) {
                                    if (!barCodeStr1.trim().equals("start")) {
                                        if (barCodeStr1.trim().length() != 0) {

                                            sb1.append(barCodeStr1.trim());

                                            num = 1;
                                            mHanlder.removeCallbacks(run_getData);
                                            mHanlder.post(run_getData);

                                             Message m = new Message();
                                             m.what = 0x123;
                                             m.obj = barCodeStr1;
                                             h.sendMessage(m);
                                        }

                                    }
                                }
                            // }
                        }
                    }
                    break;
            }
        };
    };

    int num = 1;
    Handler mHanlder = new Handler();
    Runnable run_getData = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (num > 1) {
                num = 1;
                mHanlder.removeCallbacks(run_getData);
                Message m = new Message();
                m.what = 0x123;
                Log.e("iiiiiii", "发送GET请求");
                try {
                    m.obj = sb1.toString();

                    Log.e("返回信息：", "" + m.obj);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                h.sendMessage(m);
            } else {
                num++;
                mHanlder.postDelayed(run_getData, 10);
            }
        }
    };

    // 进入App拉高55和56脚
    public static void open() {
        if(mCommonApi == null){
            return;
        }
        mCommonApi.setGpioDir(84, 1);
        mCommonApi.setGpioOut(84, 1);

    }

    // 执行扫描，即拉低74、75脚后再拉高
    public static void openScan() {

        // 发送扫描指令
        HardwareApp.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x35, 0x44, 0x4E });
    }

    // 其他地方引用mCommonApi变量
    public static CommonApi getCommonApi() {
        return mCommonApi;
    }

    public static void initGPIO() {
        if(mCommonApi == null){
            return;
        }
        mComFd = mCommonApi.openCom("/dev/ttyMT1", 115200, 8, 'N', 1);

        if (mComFd > 0) {
            //Toast.makeText(instance.app, "init success", Toast.LENGTH_SHORT).show();
            //初始化成功
        }
    }

    static Handler mHandler = new Handler();
    Runnable mRun = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            isCanSend = true;
        }
    };

    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                String hexStr = Integer.toHexString(chars[i]);
                sbu.append(hexStr).append(" ");
            }
            else {
                String hexStr = Integer.toHexString(chars[i]);
                sbu.append(hexStr+" ");
            }
        }
        return sbu.toString();
    }

    /**
     * hex String to byte array
     */
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            char[] hexChars = hexStrings[i].toCharArray();
            bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        }
        return bytes;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public static String str_tobyte(String str){


        StringBuffer s1 = new StringBuffer(str);
        StringBuffer s2 = new StringBuffer();
        int index;
        for (index = 2; index < s1.length(); index += 3) {
            s1.insert(index, ',');
        }
        String[] array = s1.toString().split(",");
        for (String string : array) {
            s2.append(string+" ");
        }

        return s2.toString();
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+" ");
        }
        return stringBuilder.toString();
    }

    public String byteToString(byte[] b, int size) {
        byte high, low;
        byte maskHigh = (byte) 0xf0;
        byte maskLow = 0x0f;

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size; i++) {
            high = (byte) ((b[i] & maskHigh) >> 4);
            low = (byte) (b[i] & maskLow);
            buf.append(findHex(high));
            buf.append(findHex(low));
            buf.append(" ");
        }
        return buf.toString();
    }

    private char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;
        if ((0 <= t) && (t <= 9)) {
            return (char) (t + '0');
        }
        return (char) (t - 10 + 'A');
    }

    /**
     * 查看一个字符串是否可以转换为数字
     *
     * @param str
     *            字符串
     * @return true 可以; false 不可以
     */
    public static boolean isStr2Num(String str) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 发送数据
     */
    public static void send(byte[] data) {
        if(mCommonApi == null){
            return;
        }
        if (data == null)
            return;
        if (mComFd > 0) {
            mCommonApi.writeCom(mComFd, data, data.length);
            Log.d(TAG, "write: " + bytesToHexString(data));
        }
    }

    private static boolean isMessyCode(String strName) {
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(strName);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();

            int length = (ch != null) ? ch.length : 0;
            for (int i = 0; i < length; i++) {
                char c = ch[i];
                if (!Character.isLetterOrDigit(c)) {
                    String str = "" + ch[i];
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String deleteErr(String str_VarMboxRead) {

        String b = str_VarMboxRead.replace("�", "");
        b = b.replace("", "");

        return b.trim();
    }

    public static void closeCommonApi() {
        if(mCommonApi == null){
            return;
        }
        if (mComFd > 0) {
            mCommonApi.setGpioMode(84, 0);
            mCommonApi.setGpioDir(84, 0);
            mCommonApi.setGpioOut(84, 0);
            mCommonApi.closeCom(mComFd);
        }

    }

    /**
     * SCAN按键的监听
     */
    class ScanBroadcastReceiver extends BroadcastReceiver {
        HardwareApp app = null;

        public ScanBroadcastReceiver (HardwareApp app) {
            this.app = app;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //openScan();
            for (int i=0;i<app.iDataReceiver_list.size();i++) {
                app.iDataReceiver_list.get(i).scanReceive();
            }
        }
    }

    public long scan_time = 0;
    public final static String NOFOUND = "nofound";
    Timer timer = new Timer();
    public final static int TIMEOUT = 3500;
    /**
     * 开始扫描
     */
    public void beginScan () {
        this.scan_time = System.currentTimeMillis();
        // 定时扫描6s, 超时报错
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 通知回调
                for (int i=0;i<iDataReceiver_list.size();i++) {
                    Intent intent = new Intent();
                    intent.putExtra("code", NOFOUND);
                    intent.setAction("com.qs.scancode");

                    if ((System.currentTimeMillis()-scan_time)>(TIMEOUT-100)) {
                        // 超时报错
                        app.sendBroadcast(intent);
                    }
                    // iDataReceiver_list.get(i).scanMsgReceive("Error");
                }
            }
        }, TIMEOUT);
    }

    /**
     * 扫描消息到达广播
     */
    class ScanMsgBroadcastReceiver extends BroadcastReceiver {
        HardwareApp app = null;

        public ScanMsgBroadcastReceiver (HardwareApp app) {
            this.app = app;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String msg = intent.getExtras().getString("code");
            msg.trim ();
            scan_time = System.currentTimeMillis();     // 扫码消息到达

            for (int i=0;i<app.iDataReceiver_list.size();i++) {
                Log.d(TAG, "list scan_msg:"+msg);
                app.iDataReceiver_list.get(i).scanMsgReceive(msg);
            }
        }
    }

    class Uart2MsgBroadcastReceiver extends BroadcastReceiver {

        HardwareApp app = null;

        public Uart2MsgBroadcastReceiver (HardwareApp app) {
            this.app = app;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String msg = intent.getExtras().getString("code");
            msg.trim ();
            Log.d(TAG, "uart2 receive:"+msg);
            for (int i=0;i<app.iDataReceiver_list.size();i++) {
                app.iDataReceiver_list.get(i).uart2MsgReceive(msg);
            }

            new Thread(() -> {
                // 广播消息
                if (app.iDataReceiver != null) {
                    app.iDataReceiver.uart2MsgReceive(msg);
                }
            }).start();
        }
    }
}