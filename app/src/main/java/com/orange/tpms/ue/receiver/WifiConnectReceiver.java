package com.orange.tpms.ue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.orange.tpms.utils.WifiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wifi状态的广播接收器
 * Created by haide.yin() on 2019/3/28 13:14.
 */
public class WifiConnectReceiver extends BroadcastReceiver {

    private IntentFilter intentFilter;//广播的靶子

    public enum WIFI_STATE{
        WIFI_STATE_DISABLED(WifiManager.WIFI_STATE_DISABLED),//WLAN已经关闭
        WIFI_STATE_DISABLING(WifiManager.WIFI_STATE_DISABLING),//WLAN正在关闭
        WIFI_STATE_ENABLED(WifiManager.WIFI_STATE_ENABLED),//WLAN已经打开
        WIFI_STATE_ENABLING(WifiManager.WIFI_STATE_ENABLING),//WLAN正在打开
        WIFI_STATE_UNKNOWN(WifiManager.WIFI_STATE_UNKNOWN);//未知

        private int wifiState;

        WIFI_STATE(int wifiState){
            this.wifiState = wifiState;
        }

        public int toInt(){
            return this.wifiState;
        }
    }

    public WifiConnectReceiver(){
        intentFilter = new IntentFilter();
        //intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); //信号强度变化
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监wifi状态，打开、关闭、正在打开、正在关闭
        //intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//网络状态变化
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化
        //intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);//是不是正在获得IP地址
        //intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            wifiStateNext(state);
        }else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){//网络列表变化了
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            //过滤空的wifi以及重名的
            List<ScanResult> results = noSameName(WifiUtils.getInstance(context).getWifiScanResult());
            //按照信号信号排序
            sortByLevel(results);
            if (success) {
                if(WifiUtils.getInstance(context).isWifiEnable()){
                    scanSuccessNext(results);
                }
            } else {
                //扫描失败可能有旧的扫描列表
                scanFailedNext(results);
            }
        }else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){//连上与否
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(NetworkInfo.State.DISCONNECTED == info.getState()){//wifi没连接上
                connecteFailedNext();
            }else if(NetworkInfo.State.CONNECTED == info.getState()){//wifi连接上了
                connecteSuccessNext();
            }else if(NetworkInfo.State.CONNECTING == info.getState()){//正在连接

            }
        }
    }

    /**
     * 外部获取Wifi靶子
     *
     * @return the intent filter
     */
    public IntentFilter getIntentFilter(){
        return this.intentFilter;
    }

    /**
     * 去除同名WIFI
     *
     * @param list 需要去除同名的列表
     * @return 返回不包含同命的列表
     */
    private List<ScanResult> noSameName(List<ScanResult> list) {
        List<ScanResult> newlist = new ArrayList<ScanResult>();
        for (ScanResult result : list) {
            //NVRAM为了特殊处理的一种情况
            if (!TextUtils.isEmpty(result.SSID) && !containName(newlist, result.SSID) && !result.SSID.contains("NVRAM")) {
                newlist.add(result);
            }
        }
        return newlist;
    }

    /**
     * 判断一个扫描结果中，是否包含了某个名称的WIFI
     *
     * @param sr 扫描结果
     * @param name 要查询的名称
     * @return 返回true表示包含了该名称的WIFI，返回false表示不包含
     */
    private boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    /**
     * 按照信号排序
     *
     * @param list 扫描结果
     */
    private void sortByLevel(List<ScanResult> list){
        Collections.sort(list, (o1, o2) -> o2.level-o1.level);
    }

    /* *********************************    Wifi连接成功  ************************************** */

    private OnConnecteSuccessListener onConnecteSuccessListener;

    public void connecteSuccessNext(){
        if(onConnecteSuccessListener != null){
            onConnecteSuccessListener.onConnecteSuccess();
        }
    }

    public void setOnConnecteSuccessListener(OnConnecteSuccessListener onConnecteSuccessListener){
        this.onConnecteSuccessListener = onConnecteSuccessListener;
    }

    public interface OnConnecteSuccessListener{
        void onConnecteSuccess();
    }

    /* *********************************  Wifi连接失败  ************************************** */

    private OnConnecteFailedListener onConnecteFailedListener;

    public void connecteFailedNext(){
        if(onConnecteFailedListener != null){
            onConnecteFailedListener.onConnecteFailed();
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
            onScanFailedListener.onScanFailed(results);
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
            onScanSuccessListener.onScanSuccess(results);
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

    public void wifiStateNext(int wifiState){
        if(onWifiStateListener != null){
            onWifiStateListener.onWifiState(wifiState);
        }
    }

    public void setOnWifiStateListener(OnWifiStateListener onWifiStateListener){
        this.onWifiStateListener = onWifiStateListener;
    }

    public interface OnWifiStateListener{
        void onWifiState(int wifiState);
    }
}
