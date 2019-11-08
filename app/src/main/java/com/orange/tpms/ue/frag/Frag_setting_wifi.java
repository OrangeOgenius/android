package com.orange.tpms.ue.frag;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.bean.PermissionBean;
import com.de.rocket.listener.PermissionListener;
import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.adapter.WifiAdapter;
import com.orange.tpms.helper.WifiConnectHelper;
import com.orange.tpms.ue.receiver.WifiConnectReceiver;
import com.orange.tpms.widget.EditDialogWidget;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import java.util.List;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_wifi extends Frag_base {

    private static final int GPS_REQUEST_CODE = 1001;

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.prl_connected)
    PercentRelativeLayout prlConnectedWifi;//ConnectedWifi
    @BindView(R.id.tv_connected)
    TextView tvConnectedWifi;//ConnectedWifi
    @BindView(R.id.rv_wifi)
    RecyclerView rvWifi;//Wifi列表
    @BindView(R.id.iv_check)
    ImageView ivWifiCheck;//Wifi开关
    @BindView(R.id.iv_check_auto)
    ImageView ivAskCheck;//Ask开关
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.edw_password)
    EditDialogWidget edwPassword;//Loading

    private WifiAdapter wifiAdapter;//适配器
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private WifiConnectHelper wifiConnectHelper;//Helper
    private View rootView;//父类的View

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_wifi;
    }

    @Override
    public String[] initPermission() {
        super.initPermission();
        setPermissionListener((i, b, list) -> {
            if(!b){
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
            //开启权限就执行
            initHelper();
        }
    }

    @Override
    public void onNexts(Object o) {

    }

    @Override
    public void onDestroyView(){//与initViewFinish对应的生命周期
        super.onDestroyView();
        wifiConnectHelper.onDestroyView(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE && isGPSOpen()){
            //开启权限就执行
            initHelper();
        }else{
            //关闭定位功能无法正常运行
            back();
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
        //设置标题
        twTitle.setTvTitle(R.string.app_wifi_setting);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //开关wifi
        ivWifiCheck.setOnClickListener((view)->{
            ivWifiCheck.setSelected(!ivWifiCheck.isSelected());
            wifiConnectHelper.switchWifi(activity,ivWifiCheck.isSelected());
        });
        //开关Ask
        ivAskCheck.setOnClickListener((view)->{
            ivAskCheck.setSelected(!ivAskCheck.isSelected());
        });
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvWifi.setLayoutManager(linearLayoutManager);
        wifiAdapter = new WifiAdapter(activity);
        rvWifi.setAdapter(wifiAdapter);
        wifiAdapter.setOnItemClickListener((pos, scanResult) ->{
            edwPassword.setObject(scanResult);
            if(WifiConnectHelper.hasPassword(activity,scanResult)){
                edwPassword.show();
            }else{
                wifiConnectHelper.connectWifi(activity,scanResult.SSID);
            }
        });
        //输入密码框
        edwPassword.setDoneListener((content) -> {
            edwPassword.hide();
            if(!TextUtils.isEmpty(content)){
                ScanResult scanResult = (ScanResult)edwPassword.getObject();
                if(scanResult != null){
                    wifiConnectHelper.connectWifi(activity,scanResult.SSID,content);
                }
            }
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        wifiConnectHelper = new WifiConnectHelper();
        //开始请求
        wifiConnectHelper.setOnPreRequestListener(()-> lwLoading.show(R.mipmap.img_wifi_connection,getResources().getString(R.string.app_wifi_connecting),true));
        //结束请求
        wifiConnectHelper.setOnFinishRequestListener(()-> lwLoading.hide());
        //wifi连接成功
        wifiConnectHelper.setOnConnecteFailedListener(()-> {

        });
        //wifi连接失败
        wifiConnectHelper.setOnConnecteSuccessListener(() -> {
            //读取当前连接的wifi
            wifiConnectHelper.getConnectedSSID(activity);
        });
        //wifi扫描失败
        wifiConnectHelper.setOnScanFailedListener((wifiList)->{
            wifiAdapter.setItems(wifiList);
            wifiAdapter.notifyDataSetChanged();
        });
        //wifi扫描成功
        wifiConnectHelper.setOnScanSuccessListener((wifiList)->{
            wifiAdapter.setItems(wifiList);
            wifiAdapter.notifyDataSetChanged();
            //读取当前连接的wifi
            wifiConnectHelper.getConnectedSSID(activity);
        });
        //wifi开关状态改变
        wifiConnectHelper.setOnWifiStateListener((state)->{
            if(state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_DISABLED.toInt()){
                ivWifiCheck.setSelected(false);
                prlConnectedWifi.setVisibility(View.GONE);
                //清空列表
                wifiAdapter.clean();
                wifiAdapter.notifyDataSetChanged();
            }else if(state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_ENABLED.toInt()){
                ivWifiCheck.setSelected(true);
                //开始扫描
                wifiConnectHelper.startScan(activity);
            }
        });
        //当前连接的wifi回调
        wifiConnectHelper.setConnectedSSIDListener((ssid)-> {
            prlConnectedWifi.setVisibility(View.VISIBLE);
            tvConnectedWifi.setText(ssid);
         });
        //初始化注册广播
        wifiConnectHelper.initViewFinish(activity);
    }
}
