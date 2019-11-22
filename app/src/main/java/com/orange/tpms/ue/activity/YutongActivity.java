package com.orange.tpms.ue.activity;

//import android.app.Instrumentation;
//import android.graphics.Color;
//import android.view.KeyEvent;
//import com.de.rocket.Rocket;
//import com.de.rocket.bean.ActivityParamBean;
//import com.de.rocket.bean.RecordBean;
//import com.de.rocket.bean.StatusBarBean;
//import com.de.rocket.ue.activity.RoActivity;
//import com.orange.tpms.R;
//import com.orange.tpms.lib.hardware.HardwareApp;
//import com.orange.tpms.ue.frag.Frag_yt_car_type;
//import com.orange.tpms.ue.frag.Frag_yt_read_sensor;
//import com.orange.tpms.ue.frag.Frag_yt_select_model;
//import com.orange.tpms.ue.frag.Frag_yt_splash;
//
//public class YutongActivity extends RoActivity {
//
//    private HardwareApp.DataReceiver dataReceiver;
//
//    // 全部显示的Frag都需要在这里注册
//    private Class[] frags = {
//            Frag_yt_splash.class,// 首页
//            Frag_yt_car_type.class,// 选择页面
//            Frag_yt_select_model.class,// 类型选择页面
//            Frag_yt_read_sensor.class,// 读取传感器页面
//    };
//
//    @Override
//    public ActivityParamBean initProperty() {
//        ActivityParamBean activityParamBean = new ActivityParamBean();
//        activityParamBean.setLayoutId(R.layout.activity_yutong);//Activity布局
//        activityParamBean.setFragmentContainId(R.id.fl_contain);//Fragment容器
//        activityParamBean.setSaveInstanceState(false);//页面不要重新创建
//        activityParamBean.setToastCustom(true);//是否用自定义的吐司风格
//        activityParamBean.setRoFragments(frags);//需要注册Fragment列表
//        activityParamBean.setShowViewBall(false);//是否显示悬浮球
//        activityParamBean.setRecordBean(new RecordBean(true,true,true,7));//日志管理
//        activityParamBean.setEnableCrashWindow(true);//是否隐藏框架自定义的崩溃的窗口
//        activityParamBean.setStatusBar(new StatusBarBean(true, Color.TRANSPARENT));//状态栏
//        return activityParamBean;
//    }
//
//    @Override
//    public void initViewFinish() {
//        //恢复状态栏,因为启动Activity的Theme里面清楚了状态栏,需要恢复
//        //<item name="android:windowFullscreen">true</item>
//        Rocket.clearWindowFullscreen(this);
//        //初始化硬件
//        HardwareApp.getInstance().switchScan(true);
//        dataReceiver = new HardwareApp.DataReceiver() {
//            @Override
//            public void scanReceive() {
//                performNavSure();
//            }
//
//            @Override
//            public void scanMsgReceive(String content) {
//
//            }
//
//            @Override
//            public void uart2MsgReceive(String content) {
//
//            }
//        };
//        HardwareApp.getInstance().addDataReceiver(dataReceiver);
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        HardwareApp.getInstance().removeDataReceiver(dataReceiver);
//        HardwareApp.getInstance().switchScan(false);
//    }
//
//    @Override
//    public void onNexts(Object o) {
//
//    }
//
//    @Override
//    public boolean onBackClick() {
//        return false;
//    }
//
//    /**
//     * 模拟导航栏的确认
//     */
//    private void performNavSure(){
//        new Thread(() -> {
//            Instrumentation inst = new Instrumentation();
//            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);  //传入不同的keycode就ok了
//        }).start();
//    }
//}
