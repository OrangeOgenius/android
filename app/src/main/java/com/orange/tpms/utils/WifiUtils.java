package com.orange.tpms.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Wifi连接的相关类
 *
 * 两个危险权限需要动态申请
 * private static final String[] NEEDED_PERMISSIONS = new String[]{
 *     Manifest.permission.ACCESS_COARSE_LOCATION,
 *     Manifest.permission.ACCESS_FINE_LOCATION
 * };
 *
 * 需要申明的的权限
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *
 * 需要注册以下广播
 * WifiConnectReceiver wifiReceiver = new WifiConnectReceiver();
 * IntentFilter filter = new IntentFilter();
 * filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
 * filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态广播,是否连接了一个有效路由
 * filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
 * registerReceiver(wifiReceiver, filter);
 *
 * Created by haide.yin() on 2019/3/26 14:32.
 */
public class WifiUtils {

    private static volatile WifiUtils instance;//单例
    private WifiManager mWifiManager;// 定义WifiManager对象

    private WifiUtils(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 单例
     * @param context the context
     * @return the instance
     */
    public static WifiUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtils.class) {
                if (instance == null) {
                    instance = new WifiUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 连接Wifi
     * @param name 用户名
     * @param password 密码
     * @param wifiCipherType 加密类型
     */
    public void connectWifi(String name,String password,WifiCipherType wifiCipherType){
        WifiConfiguration wifiConfiguration = createWifiConfig(name,password,wifiCipherType);
        addNetWork(wifiConfiguration);
        /*WifiConfiguration tempConfig  = isExsits(name);
        if(tempConfig == null){
            WifiConfiguration wifiConfiguration = createWifiConfig(name,password,wifiCipherType);
            addNetWork(wifiConfiguration);
        }else{
            addNetWork(tempConfig);
        }*/
    }

    /**
     * 扫描Wifi
     */
    public void startScan(){
        if (mWifiManager != null) {
            mWifiManager.startScan();
        }
    }

    /**
     * 读取Wifi列表
     * @return the wifi scan result
     */
    public List<ScanResult> getWifiScanResult() {
        if (mWifiManager != null) {
            return mWifiManager.getScanResults();
        }
        return null;
    }

    /**
     * 获取当前连接WIFI的SSID
     */
    public String getConnectedSSID() {
        WifiInfo winfo = getConnectionInfo();
        if (winfo != null) {
            String s = winfo.getSSID();
            if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                return s.substring(1, s.length() - 1);
            }
        }
        return "";
    }

    /**
     * 取得WifiInfo对象
     * @return the wifi connection info
     */
    public WifiInfo getConnectionInfo() {
        if (mWifiManager != null) {
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    /**
     * Wifi是否开启
     * @return the boolean
     */
    public boolean isWifiEnable() {
        if (mWifiManager != null) {
            return mWifiManager.isWifiEnabled();
        }
        return false;
    }

    /**
     * Wifi配置列表
     * @return the configurations
     */
    public List getConfigurations() {
        if (mWifiManager != null) {
            return mWifiManager.getConfiguredNetworks();
        }
        return null;
    }

    /**
     * 创建Wifi配置信息
     * @param SSID     Wifi名
     * @param password Wifi密码
     * @param type     加密方式
     * @return the wifi configuration
     */
    private WifiConfiguration createWifiConfig(String SSID, String password, WifiCipherType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
            //config.wepKeys[0] = "";  //注意这里
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 接入某个wifi热点
     * @param config  the config
     * @return the boolean
     */
    private boolean addNetWork(WifiConfiguration config) {
        boolean result = false;
        if (mWifiManager != null && mWifiManager.getConnectionInfo() != null) {
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            if (config.networkId > 0) {
                result = mWifiManager.enableNetwork(config.networkId, true);
                mWifiManager.updateNetwork(config);
            } else {
                int i = mWifiManager.addNetwork(config);
                if (i > 0) {
                    mWifiManager.saveConfiguration();
                    result = mWifiManager.enableNetwork(i, true);
                }
            }
        }
        return result;
    }

    /**
     * 判断wifi热点支持的加密方式
     * @param s the s
     * @return the wifi cipher
     */
    public WifiCipherType getWifiCipher(String s) {
        if (s.isEmpty()) {
            return WifiCipherType.WIFICIPHER_INVALID;
        } else if (s.contains("WEP")) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else if (s.contains("WPA") || s.contains("WPA2") || s.contains("WPS")) {
            return WifiCipherType.WIFICIPHER_WPA;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    /**
     * 查看以前是否也配置过这个网络
     * @param SSID    the ssid
     * @return the wifi configuration
     */
    private WifiConfiguration isExsits(String SSID) {
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 打开WIFI
     */
    public void openWifi() {
        if (mWifiManager != null) {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        }
    }

    /**
     * 关闭WIFI
     */
    public void closeWifi() {
        if (mWifiManager != null) {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * Wifi是否打开
     * @return the boolean
     */
    public boolean isOpenWifi() {
        if (mWifiManager != null) {
            return mWifiManager.isWifiEnabled();
        }
        return false;
    }

    /**
     * Wifi加密类型
     */
    public enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID
    }
}