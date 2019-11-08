package com.orange.tpms.ue.activity;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.de.rocket.Rocket;
import com.de.rocket.bean.ActivityParamBean;
import com.de.rocket.bean.RecordBean;
import com.de.rocket.bean.StatusBarBean;
import com.de.rocket.ue.activity.RoActivity;
import com.orange.blelibrary.blelibrary.BleActivity;
import com.orange.tpms.R;
import com.orange.tpms.mmySql.ItemDAO;
import com.orange.tpms.ue.frag.Frag_base;
import com.orange.tpms.ue.frag.Frag_car_model;
import com.orange.tpms.ue.frag.Frag_car_year;
import com.orange.tpms.ue.frag.Frag_check_sensor;
import com.orange.tpms.ue.frag.Frag_id_copy_detail;
import com.orange.tpms.ue.frag.Frag_id_copy_new;
import com.orange.tpms.ue.frag.Frag_id_copy_original;
import com.orange.tpms.ue.frag.Frag_home;
import com.orange.tpms.ue.frag.Frag_id_copy;
import com.orange.tpms.ue.frag.Frag_language;
import com.orange.tpms.ue.frag.Frag_login;
import com.orange.tpms.ue.frag.Frag_low_frequency;
import com.orange.tpms.ue.frag.Frag_program_number_choice;
import com.orange.tpms.ue.frag.Frag_policy;
import com.orange.tpms.ue.frag.Frag_program_detail;
import com.orange.tpms.ue.frag.Frag_program_sensor;
import com.orange.tpms.ue.frag.Frag_register;
import com.orange.tpms.ue.frag.Frag_reset;
import com.orange.tpms.ue.frag.Frag_scan_info;
import com.orange.tpms.ue.frag.Frag_check_sensor_information;
import com.orange.tpms.ue.frag.Frag_check_sensor_location;
import com.orange.tpms.ue.frag.Frag_check_sensor_read;
import com.orange.tpms.ue.frag.Frag_setting;
import com.orange.tpms.ue.frag.Frag_setting_area;
import com.orange.tpms.ue.frag.Frag_setting_autolock;
import com.orange.tpms.ue.frag.Frag_setting_bluebud;
import com.orange.tpms.ue.frag.Frag_car_makes;
import com.orange.tpms.ue.frag.Frag_favourite;
import com.orange.tpms.ue.frag.Frag_setting_favourite;
import com.orange.tpms.ue.frag.Frag_setting_infomation;
import com.orange.tpms.ue.frag.Frag_setting_system_reset;
import com.orange.tpms.ue.frag.Frag_setting_system_update;
import com.orange.tpms.ue.frag.Frag_setting_tips;
import com.orange.tpms.ue.frag.Frag_setting_unit;
import com.orange.tpms.ue.frag.Frag_setting_language;
import com.orange.tpms.ue.frag.Frag_setting_policy;
import com.orange.tpms.ue.frag.Frag_setting_sounds;
import com.orange.tpms.ue.frag.Frag_setting_wifi;
import com.orange.tpms.ue.frag.Frag_splash;
import com.orange.tpms.ue.frag.Frag_wifi;

import java.util.List;

/**
 * 首次启动的Activity
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class MainActivity extends RoActivity {
    public ItemDAO  itemDAO= null ;
    public static String SelectMake="";
    public static String SelectModel="";
    public static String SelectYear="";
    public static int position=1;
    public static int ProgramNumber=1;
    public final static int 檢查傳感器=1;
    public final static int 燒錄傳感器=2;
    public final static int 複製傳感器=3;
    public static int ScanType=1;
    public static List<String> SensorList=null;
    public static List<String> NewSensorList=null;
    // 全部显示的Frag都需要在这里注册
    private Class[] frags = {
            // TODO: 2019/9/3 临时加的
            //Frag_login.class,//登录页面
            
            Frag_splash.class,// 调试页
            Frag_home.class,//主页
            Frag_setting_system_update.class,//设置-系统设置-升级
            Frag_program_detail.class,//烧录详情页
            Frag_check_sensor_read.class,//取Sensor信息读
            Frag_check_sensor_location.class,//取Sensor轮胎情况
            Frag_program_sensor.class,//变成
            Frag_check_sensor_information.class,//Sensor信息
            Frag_wifi.class,//Wifi连接
            Frag_setting.class,//设置
            Frag_login.class,//登录页面
            Frag_register.class,//注册
            Frag_reset.class,//找回密码
            Frag_policy.class,//隐私
            Frag_setting_favourite.class,//设置-我的最爱
            Frag_setting_wifi.class,//设置-Wifi
            Frag_setting_tips.class,//设置-系统设置
            Frag_setting_system_reset.class,//设置-系统设置-重置
            Frag_setting_language.class,//设置-语言
            Frag_setting_infomation.class,//设置-信息
            Frag_setting_autolock.class,//设置-自动锁
            Frag_setting_sounds.class,//设置-声音
            Frag_setting_unit.class,//设置-jnit
            Frag_setting_bluebud.class,//设置-蓝牙
            Frag_check_sensor.class,//检查
            Frag_id_copy_detail.class,//ID COPY详情页
            Frag_id_copy_original.class,//ID COPY原始页面
            Frag_id_copy_new.class,//ID COPY新页面
            Frag_program_number_choice.class,//选择号码
            Frag_car_makes.class,// 选车
            Frag_car_model.class,// 选车型
            Frag_car_year.class,// 选车年份
            Frag_favourite.class,//最爱
            Frag_language.class,//语言选择
            Frag_id_copy.class,//拷贝
            Frag_setting_policy.class,//设置-隐私
            Frag_scan_info.class,//扫码介绍页面
            Frag_low_frequency.class,//低频设置
            Frag_setting_area.class,//区域选择
    };

    @Override
    public ActivityParamBean initProperty() {
        ActivityParamBean activityParamBean = new ActivityParamBean();
        activityParamBean.setLayoutId(R.layout.activity_main);//Activity布局
        activityParamBean.setFragmentContainId(R.id.fl_contain);//Fragment容器
        activityParamBean.setSaveInstanceState(false);//页面不要重新创建
        activityParamBean.setToastCustom(true);//是否用自定义的吐司风格
        activityParamBean.setRoFragments(frags);//需要注册Fragment列表
        activityParamBean.setShowViewBall(false);//是否显示悬浮球
        activityParamBean.setRecordBean(new RecordBean(true,true,true,7));//设置日志策略
        activityParamBean.setEnableCrashWindow(true);//是否隐藏框架自定义的崩溃的窗口
        activityParamBean.setStatusBar(new StatusBarBean(true, Color.TRANSPARENT));//状态栏
        return activityParamBean;
    }

    @Override
    public void initViewFinish() {
        //恢复状态栏,因为启动Activity的Theme里面清楚了状态栏,需要恢复
        //<item name="android:windowFullscreen">true</item>
        Rocket.clearWindowFullscreen(this);
        /*HardwareApp.getInstance().switchScan(true);
        HardwareApp.getInstance().addDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {
                performNavSure();
            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {

            }
        });*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //HardwareApp.getInstance().switchScan(false);
    }

    @Override
    public void onNexts(Object o) {

    }

    @Override
    public boolean onBackClick() {
        return false;
    }

    /**
     * 模拟导航栏的确认
     */
    private void performNavSure(){
        new Thread(() -> {
            Instrumentation inst = new Instrumentation();
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);  //传入不同的keycode就ok了
        }).start();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){//只处理按下的动画,抬起的动作忽略
            Log.v("yhd-","event:"+event.toString());
            //按键事件向Fragment分发
            Fragment topRocketStackFragment = Rocket.getTopRocketStackFragment(this);
            //页面在顶层才会分发
            if(topRocketStackFragment instanceof Frag_base){
                ((Frag_base)topRocketStackFragment).dispatchKeyEvent(event);
            }
        }
        return superDispatchKeyEvent(event);
    }
}
