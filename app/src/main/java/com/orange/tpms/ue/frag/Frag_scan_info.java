package com.orange.tpms.ue.frag;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.bean.MMYQrCodeBean;
import com.orange.tpms.bean.ScanQrCodeBean;
import com.orange.tpms.bean.SensorQrCodeBean;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_scan_info extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.iv_scan_tips)
    ImageView ivTips;//提示的图片
    @BindView(R.id.tv_info_tips)
    TextView tvTips;//提示的文字
    @BindView(R.id.bt_tigger)
    Button btTrigger;//模拟扫码
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading

    private ScanQrCodeBean scanQrCodeBean = new ScanQrCodeBean(ScanQrCodeBean.TYPE_MMY);
    private HardwareApp.DataReceiver dataReceiver;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_scan_info;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {
        scanQrCodeBean.setScanType(MainActivity.ScanType);
            twTitle.setTvTitle(R.string.app_scan_code);
            if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_MMY){
                ivTips.setImageResource(R.mipmap.iv_scan_mmy);
                tvTips.setText(R.string.app_scan_tips);
            }else if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_SENSORID) {
                ivTips.setImageResource(R.mipmap.iv_scan_sensorid);
                tvTips.setText(R.string.app_scan_and_packing);
            }
    }

    @Override
    public void onDestroyView(){//与initViewFinish对应的生命周期
        super.onDestroyView();
        Log.v("yhd-","Frag_scan_info:"+"onDestroyView");
        HardwareApp.getInstance().switchScan(false);
        HardwareApp.getInstance().removeDataReceiver(dataReceiver);
    }

    @Event(R.id.bt_tigger)
    private void trigger(View view){
        if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_MMY){
            backToLastFrag("300100D**2C");
        }else if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_SENSORID){
            //backToLastFrag(":0EF45f9A:T1904116-1:SP201(NEW):");
            backToLastFrag("*0EF45f9A*");
        }
    }

    @Override
    public void onKeyScan(){
        Rocket.writeOuterLog("Frag_scan_info::onKeyScan");
        HardwareApp.getInstance().scan();
        lwLoading.show(getResources().getString(R.string.app_scaning));
    }

    /**
     * 返回
     */
    public void backToLastFrag(String content){
        Log.v("yhd-","content："+content);
        Log.v("yhd-","scanQrCodeBean.getScanType()："+scanQrCodeBean.getScanType());
        if(!TextUtils.isEmpty(content)){
            if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_MMY){
                if(!content.contains("**")){
                    toast(R.string.app_invalid_mmy_qrcode);
                }else{
                    scanQrCodeBean.setMmyQrCodeBean(MMYQrCodeBean.toQRcodeBean(content));
                    Log.v("yhd-","scanQrCodeBean："+scanQrCodeBean.getMmyQrCodeBean().getMmyNumber());
                    ((MainActivity)activity).itemDAO.GoOk(scanQrCodeBean.getMmyQrCodeBean().getMmyNumber(),this);
                    back(false,scanQrCodeBean);
                }
            }else if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_SENSORID){
                if(!content.contains(":") && !content.contains("*")){
                    toast(R.string.app_invalid_sensor_qrcode);
                }else{
                    scanQrCodeBean.setSensorQrCodeBean(SensorQrCodeBean.toQRcodeBean(content));
                    Log.v("yhd-","scanQrCodeBean："+scanQrCodeBean.toString());
                    back(false,scanQrCodeBean);
                }
            }
        }else{
            toast(R.string.app_content_empty);
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        btTrigger.setVisibility(HardwareApp.getInstance().isEnableHareware()?View.INVISIBLE:View.VISIBLE);
        //返回
        twTitle.setOnBackListener((view) -> back());
        HardwareApp.getInstance().switchScan(true);
        dataReceiver = new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {
                Rocket.writeOuterLog("Frag_scan_info::scanMsgReceive->content:"+content);
                lwLoading.hide();
                //避免回调两次出现回退异常,判断当前页面在栈顶才会跳转
                if(Rocket.isTopRocketStack(Frag_scan_info.class.getSimpleName())){
                    Log.v("yhd-","backToLastFrag:"+content);
                    backToLastFrag(content);
                }
            }

            @Override
            public void uart2MsgReceive(String content) {

            }
        };
        HardwareApp.getInstance().addDataReceiver(dataReceiver);
    }
}
