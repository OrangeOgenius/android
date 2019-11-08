package com.orange.tpms.ue.frag;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.WifiConnectHelper;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.receiver.WifiConnectReceiver;
import com.orange.tpms.utils.OggUtils;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

import com.de.rocket.ue.injector.BindView;

/**
 * Wifi连接页面
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_wifi extends Frag_base {

    private static final int GPS_REQUEST_CODE = 1001;

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.et_wifi_password)
    EditText etWifiPassword;//密码
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.sp_wifi_name)
    NiceSpinner spWifiName;//State

    private WifiConnectHelper wifiHelper;//Helper
    private boolean isStartConnected;//开始连接
    private String wifiName;//wifi name
    private boolean hasList = false;//是否已经拿到列表

    @Override
    public int onInflateLayout() {
        return R.layout.frag_wifi;
    }

    @Override
    public String[] initPermission() {
        super.initPermission();
        setPermissionListener((i, b, list) -> {
            if(!b){
                toast("没有定位权限");
                back();
            }
        });
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        if(!isGPSOpen()){
            //跳转到手机原生设置页面,打开定位功能
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,GPS_REQUEST_CODE);
        }else{
            initHelper();
        }
    }

    @Override
    public void onDestroyView(){//与initViewFinish对应的生命周期
        super.onDestroyView();
        if(wifiHelper != null){
            wifiHelper.onDestroyView(activity);
        }
    }

    @Override
    public void onNexts(Object o) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE && isGPSOpen()){
            //打开定位就执行
            initHelper();
        }else{
            //关闭定位功能无法正常运行
            back();
        }
    }

    @Event(R.id.bt_cancel)
    private void cancel(View view){
        back();
    }

    @Event(R.id.bt_select)
    private void connect(View view){
        if(!TextUtils.isEmpty(wifiName) && !OggUtils.isEmpty(etWifiPassword)){
            wifiHelper.connectWifi(activity,wifiName,etWifiPassword.getText().toString());
            isStartConnected = true;
        }else{
            toast(R.string.app_content_empty,2000);
        }
    }

    /**
     * 检查有没打开定位
     */
    private boolean isGPSOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置Loading提示
        lwLoading.getTvLoading().setVisibility(View.GONE);
        //设置标题
        twTitle.setTvTitle(R.string.app_wifi_settings);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //选中
        spWifiName.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            wifiName = (String) parent.getItemAtPosition(position);
        });
        //etWifiPassword.setText("hj123302687..");
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        wifiHelper = new WifiConnectHelper();
        //开始连接
        wifiHelper.setOnPreRequestListener(()-> lwLoading.show(R.mipmap.img_wifi_connection,getResources().getString(R.string.app_wifi_connecting),true));
        //连接完成
        wifiHelper.setOnFinishRequestListener(()-> lwLoading.hide());
        //连接成功
        wifiHelper.setOnConnecteSuccessListener(()->{
            toast(R.string.app_wifi_connected);
            toFrag(Frag_login.class,true,true,null);
        });
        //连接失败
        wifiHelper.setOnConnecteFailedListener(()->{
            if(isStartConnected){
                toast(R.string.app_connected_failed,2000);
                isStartConnected = false;
            }
        });
        //wifi扫描失败
        wifiHelper.setOnScanFailedListener((wifiList)->{
            toast(R.string.app_wiFi_scan_failed);
            spWifiName.setVisibility(View.GONE);
        });
        //wifi扫描成功
        wifiHelper.setOnScanSuccessListener((wifiList)->{
            if(hasList){
                return;
            }
            spWifiName.setVisibility(View.VISIBLE);
            List<String> ssidList = new ArrayList<>();
            for(ScanResult scanResult : wifiList){
                String name = scanResult.SSID;
                if(!TextUtils.isEmpty(name)){
                    if(name.contains("\"")){
                        name = name.replace("\"","");
                    }
                }
                ssidList.add(name);
            }
            if(ssidList.size() > 0){
                hasList = true;
                wifiName = ssidList.get(0);
                spWifiName.attachDataSource(ssidList);
            }
        });
        //wifi开关状态改变
        wifiHelper.setOnWifiStateListener((state)->{
            if(state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_ENABLED.toInt()){
                wifiHelper.startScan(activity);
            }
        });
        //初始化注册广播
        if(WifiConnectHelper.isNetworkConnected(activity)){
            toFrag(Frag_login.class,true,true,null);
        }else{
            //注册广播
            wifiHelper.initViewFinish(activity);
            //打开wifi
            wifiHelper.switchWifi(activity,true);
        }
    }
}
