package com.orange.tpms.ue.frag;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.ReadSensorHelper;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.VibMediaUtil;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.ScanWidget;
import com.orange.tpms.widget.TitleWidget;

import bean.mmy.MMyBean;
import com.de.rocket.ue.injector.BindView;

/**
 * Sensor Information
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_check_sensor_read extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.tv_id)
    TextView tvID;//ID
    @BindView(R.id.tv_kpa)
    TextView tvKpa;//Kpa
    @BindView(R.id.tv_c)
    TextView tvC;//C
    @BindView(R.id.tv_bat)
    TextView tvBAT;//BAT
    @BindView(R.id.tv_vol)
    TextView tvVoltage;//Voltage
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.tv_content)
    TextView tvContent;//Title
    @BindView(R.id.editText)
    EditText editText;//Title
    @BindView(R.id.scw_tips)
    ScanWidget scwTips;//Tips
    String ObdHex="00";
    private ReadSensorHelper readSensorHelper;//Helper
    private VibMediaUtil vibMediaUtil;//音效与振动

    @Override
    public int onInflateLayout() {
        return R.layout.frag_check_sensor_read;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onDestroyView(){//与initViewFinish对应的生命周期
        super.onDestroyView();
        vibMediaUtil.release();
    }

    @Override
    public void onNexts(Object o) {
    }

    @Override
    public void onKeyTrigger(){
        if(scwTips.isShown()){
            scwTips.hide();
        }
        //避免回调两次出现异常,判断当前页面在栈顶才会读取
        if(Rocket.isTopRocketStack(Frag_check_sensor_read.class.getSimpleName())){
            trigger();
        }
    }

    @Override
    public boolean onBackPresss(){
        if(scwTips.isShown()){
            scwTips.hide();
        }else{
            back();
        }
        return true;
    }

    @Event(R.id.bt_tigger)
    private void trigger(View view){
        trigger();
    }


    @Event(R.id.bt_menue)
    private void menue(View view){
        toFrag(Frag_home.class,true,true,null,true);
    }

    /**
     * 读传感器
     */
    private void trigger(){
        tvID.setText("");
        tvKpa.setText("");
        tvC.setText("");
        tvBAT.setText("");
        tvVoltage.setText("");
        readSensorHelper.readSensor(1,ObdHex,editText.getText().toString());
        vibMediaUtil.playVibrate();
    }

    /**
     * 初始化页面
     */
    private void initView() {
//        ObdHex=((MainActivity)activity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear);
        Log.d("obdhex","hex"+ObdHex);
        tvContent.setText(ObdHex);
        //设置标题
        twTitle.setTvTitle(R.string.app_sensor_info_read);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //音效与震动
        vibMediaUtil = new VibMediaUtil(activity);
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        readSensorHelper = new ReadSensorHelper();
        //开始请求
        readSensorHelper.setOnPreRequestListener(() -> lwLoading.show(getResources().getString(R.string.app_data_reading)));
        //结束请求
        readSensorHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //请求成功
        readSensorHelper.setOnSuccessRequestListener((object -> toFrag(Frag_home.class,false,true,null)));
        //请求失败
        readSensorHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //读取四个轮胎情况
        readSensorHelper.setReadSensorListener((sensorDataBean) -> {
            vibMediaUtil.playBeep();
            tvID.setText(String.valueOf(sensorDataBean.getSensor_id()));
            tvKpa.setText(String.valueOf(sensorDataBean.getKpa()));
            tvC.setText(String.valueOf(sensorDataBean.getTemp()));
            tvBAT.setText(String.valueOf(sensorDataBean.getBatteryLevel()));
            tvVoltage.setText(String.valueOf(sensorDataBean.getV()));
        });
        //读取失败
        readSensorHelper.setOnReadFailedListener(() -> lwLoading.show(R.mipmap.img_update_failed,getResources().getString(R.string.app_read_failed),false));
    }
}
