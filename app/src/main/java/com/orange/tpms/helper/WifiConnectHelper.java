package com.orange.tpms.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import com.orange.tpms.ue.receiver.WifiConnectReceiver;
import com.orange.tpms.utils.WifiUtils;

import java.util.List;

/**
 * Wifi连接相关类
 * Created by haide.yin() on 2019/4/3 17:50.
 */
public class WifiConnectHelper extends BaseHelper {

    private WifiConnectReceiver wifiBroadcastReceiver;//Wifi连接的广播

    /**
     * 网络是否可用
     * @param context 上下文
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 连接Wifi
     * @param context 上下文
     * @param ssid wifiSID
     * @param password wifiPassword
     */
    public void connectWifi(Context context,String ssid,String password){
        preRequestNext();
        if(!WifiUtils.getInstance(context).isOpenWifi()){
            WifiUtils.getInstance(context).openWifi();
        }
        WifiUtils.getInstance(context).connectWifi(ssid,password,WifiUtils.WifiCipherType.WIFICIPHER_WPA);
    }

    /**
     * 连接Wifi,没有密码的
     * @param context 上下文
     * @param ssid wifiSID
     */
    public void connectWifi(Context context,String ssid){
        preRequestNext();
        if(!WifiUtils.getInstance(context).isOpenWifi()){
            WifiUtils.getInstance(context).openWifi();
        }
        WifiUtils.getInstance(context).connectWifi(ssid,"",WifiUtils.WifiCipherType.WIFICIPHER_NOPASS);
    }

    /**
     * 扫描Wifi
     * @param context 上下文
     */
    public void startScan(Context context){
        preRequestNext();
        if(!WifiUtils.getInstance(context).isOpenWifi()){
            WifiUtils.getInstance(context).openWifi();
        }
        WifiUtils.getInstance(context).startScan();
    }

    /**
     * 获取当前连接WIFI的SSID
     * @param context 上下文
     */
    public void getConnectedSSID(Context context) {
        connectedSSIDNext(WifiUtils.getInstance(context).getConnectedSSID());
    }

    /**
     * 开关Wifi
     * @param context 上下文
     */
    public void switchWifi(Context context,boolean isOn){
        if(isOn){
            WifiUtils.getInstance(context).openWifi();
        }else{
            WifiUtils.getInstance(context).closeWifi();
        }
    }

    /**
     * Wifi时候需要密码
     * @param context 上下文
     * @param scanResult 是否有密码
     */
    public static boolean hasPassword(Context context,ScanResult scanResult){
        if(scanResult != null){
            WifiUtils.WifiCipherType wifiCipherType = WifiUtils.getInstance(context).getWifiCipher(scanResult.capabilities);
            if(wifiCipherType == WifiUtils.WifiCipherType.WIFICIPHER_NOPASS){
                return false;
            }
        }
        return true;
    }


    /**
     * 页面隐藏取消注册广播
     * @param context 上下文
     */
    public void onDestroyView(Context context) {
        if(wifiBroadcastReceiver != null){
            context.unregisterReceiver(wifiBroadcastReceiver);
        }
    }

    /**
     * 页面初始化注册广播
     * @param context 上下文
     */
    public void initViewFinish(Context context) {
        //注册广播
        wifiBroadcastReceiver = new WifiConnectReceiver();
        //连接成功
        wifiBroadcastReceiver.setOnConnecteSuccessListener(() -> {
            finishRequestNext();
            connecteSuccessNext();
        });
        //连接失败
        wifiBroadcastReceiver.setOnConnecteFailedListener(() -> {
            finishRequestNext();
            connecteFailedNext();
        });
        //扫描成功
        wifiBroadcastReceiver.setOnScanSuccessListener((wifiList)->{
            finishRequestNext();
            scanSuccessNext(wifiList);
        });
        //扫描失败
        wifiBroadcastReceiver.setOnScanFailedListener((wifiList)->{
            finishRequestNext();
            scanFailedNext(wifiList);
        });
        //wifi开关改变
        wifiBroadcastReceiver.setOnWifiStateListener(this::WifiStateNext);
        //注册广播
        context.registerReceiver(wifiBroadcastReceiver, wifiBroadcastReceiver.getIntentFilter());
    }


    /* *********************************  连接成功  ************************************** */

    private OnConnecteSuccessListener onConnecteSuccessListener;

    public void connecteSuccessNext(){
        if(onConnecteSuccessListener != null){
            runMainThread(() -> onConnecteSuccessListener.onConnecteSuccess());
        }
    }

    public void setOnConnecteSuccessListener(OnConnecteSuccessListener onConnecteSuccessListener){
        this.onConnecteSuccessListener = onConnecteSuccessListener;
    }

    public interface OnConnecteSuccessListener{
        void onConnecteSuccess();
    }

    /* *********************************  连接成功  ************************************** */

    private OnConnecteFailedListener onConnecteFailedListener;

    public void connecteFailedNext(){
        if(onConnecteFailedListener != null){
            runMainThread(() -> onConnecteFailedListener.onConnecteFailed());
        }
    }

    public void setOnConnecteFailedListener(OnConnecteFailedListener onConnecteFailedListener){
        this.onConnecteFailedListener = onConnecteFailedListener;
    }

    public interface OnConnecteFailedListener{
        void onConnecteFailed();
    }

    /* *********************************  扫描列表失败  ************************************** */

    private OnScanFailedListener onScanFailedListener;

    public void scanFailedNext(List<ScanResult> results){
        if(onScanFailedListener != null){
            runMainThread(() -> onScanFailedListener.onScanFailed(results));
        }
    }

    public void setOnScanFailedListener(OnScanFailedListener onScanFailedListener){
        this.onScanFailedListener = onScanFailedListener;
    }

    public interface OnScanFailedListener{
        void onScanFailed(List<ScanResult> results);
    }

    /* *********************************  扫描列表成功  ************************************** */

    private OnScanSuccessListener onScanSuccessListener;

    public void scanSuccessNext(List<ScanResult> results){
        if(onScanSuccessListener != null){
            runMainThread(() -> onScanSuccessListener.onScanSuccess(results));
        }
    }

    public void setOnScanSuccessListener(OnScanSuccessListener onScanSuccessListener){
        this.onScanSuccessListener = onScanSuccessListener;
    }

    public interface OnScanSuccessListener{
        void onScanSuccess(List<ScanResult> results);
    }

    /* *********************************  Wifi状态切换  ************************************** */

    private OnWifiStateListener onWifiStateListener;

    public void WifiStateNext(int wifiState){
        if(onWifiStateListener != null){
            runMainThread(() -> onWifiStateListener.onWifiState(wifiState));
        }
    }

    public void setOnWifiStateListener(OnWifiStateListener onWifiStateListener){
        this.onWifiStateListener = onWifiStateListener;
    }

    public interface OnWifiStateListener{
        void onWifiState(int wifiState);
    }

    /* *********************************  读取已经连接的wifi SSID  ************************************** */

    private OnConnectedSSIDListener onConnectedSSIDListener;

    public void connectedSSIDNext(String ssid){
        if(onConnectedSSIDListener != null){
            runMainThread(() -> onConnectedSSIDListener.onConnectedSSID(ssid));
        }
    }

    public void setConnectedSSIDListener(OnConnectedSSIDListener onConnectedSSIDListener){
        this.onConnectedSSIDListener = onConnectedSSIDListener;
    }

    public interface OnConnectedSSIDListener{
        void onConnectedSSID(String ssid);
    }
}
