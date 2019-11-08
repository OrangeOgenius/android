package com.orange.tpms.ue.frag;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.helper.LoginHelper;
import com.orange.tpms.helper.SystemUpdateHelper;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.mmySql.ItemDAO;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.FileDowload;
import com.orange.tpms.utils.NumberUtil;
import com.orange.tpms.widget.LoadingPointWidget;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.NormalDialogWidget;

/**
 * 启动页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_splash extends Frag_base {

    @BindView(R.id.ldw_loading)
    LoadingPointWidget lwLoading;//Loading
    @BindView(R.id.ndw_tip)
    NormalDialogWidget ndwTip;//ndwTip
    @BindView(R.id.ldw_update_loading)
    LoadingWidget lwUpdateLoading;//Loading

    private SystemUpdateHelper systemUpdateHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_splash;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initHelper();
        initView();
    }

    @Override
    public void onNexts(Object o) {
        //硬件还没完全准备好的时候调用initWithCb没有回调
        new Handler().postDelayed(this::initConfig,0);
    }

    @Override
    public boolean onBackPresss(){
        if(HardwareApp.getInstance().isEnableHareware()){
            HardwareApp.getInstance().unRegisterBrocast();
        }
        System.exit(0);
        Process.killProcess(Process.myPid());
        return true;
    }

    /**
     * 初始化配置信息
     */
    private void initConfig(){
        //初始化MMY以及市区数据
        showLoading(true);
        initMMY(() -> initZoneTable(() -> {
            //如果开启硬件行为则需要访问硬件的状态
            if(HardwareApp.getInstance().isEnableHareware()){
                Rocket.writeOuterLog("获取硬件的状态-initWithCb");
                HardwareApp.getInstance().initWithCb(activity, new HardwareApp.InitCb() {
                    @Override
                    public void onStart() {
                        //与模块握手中...
                        Rocket.writeOuterLog("获取硬件的状态-initWithCb-onStart");
                    }

                    @Override
                    public void pingReceive(int ret) {
                        Rocket.writeOuterLog("获取硬件的状态-initWithCb-pingReceive：ret："+ret);
                        showLoading(false);
                        if(ret == 1){
                            toast(R.string.app_bootaloader_mode);
                            //toFrag(Frag_setting_system_update.class,true,true,null);
                            systemUpdateHelper.updateFlash();
                        }else{
                            if(systemUpdateHelper.getIfSystemAutoUpdate(activity)){
                                //初始化完毕开始检测是否有更新
                                systemUpdateHelper.checkUpdate(activity);
                            }else{
                                initFinish();
                            }
                        }
                    }
                });
            }else{
                Rocket.writeOuterLog("没有开启硬件");
                showLoading(false);
                if(systemUpdateHelper.getIfSystemAutoUpdate(activity)){
                    //初始化完毕开始检测是否有更新
                    systemUpdateHelper.checkUpdate(activity);
                }else{
                    initFinish();
                }
            }
        }));
    }

    /**
     * 加载MMY表
     */
    private void initMMY(ResultCallback resultCallback){
        //加载MMY


        new Thread(() -> MMy.LoadMMy(activity, new MMy.MMyLoadCb() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                resultCallback.onSuccess();
                ((MainActivity)activity).itemDAO=new ItemDAO(activity);
                Log.d("mmy",((MainActivity)activity).itemDAO.getMake(activity).get(0).getMake()+"");
            }

            @Override
            public void onProgress(int total, int progress) {
                //String content = NumberUtil.toFormate((float) progress/total * 100);
            }
        })).start();
    }

    /**
     * 加载地区表
     */
    private void initZoneTable(ResultCallback resultCallback){
        // TODO: 2019/9/3 zeqiang 初始化时间过长 每次都要初始化
        resultCallback.onSuccess();
        /*new Thread(() -> {
            //将时区数据库文件加载到sqlite
            ZoneTable.initData(activity, new ZoneTable.SQLInitCb() {
                @Override
                public void onStart() {
                    updateLoading(String.valueOf("开始加载地区表"));
                }

                @Override
                public void onStop() {
                    resultCallback.onSuccess();
                }

                @Override
                public void onProgress(String s, int total, int progress) {
                    DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String content = decimalFormat.format((float) progress/total * 100);//format 返回的是字符串
                    updateLoading(String.valueOf("加载地区表("+content+")"));
                }

                @Override
                public void onFail(int total, int progress, Exception e) {
                    //加载失败默许跳过
                    resultCallback.onSuccess();
                }
            });
        }).start();*/
    }

    /**
     * 显示Loading
     */
    private void showLoading(boolean show){
        activity.runOnUiThread(() -> {
            if(show){
                lwLoading.show();
            }else{
                lwLoading.hide();
            }
        });
    }

    /**
     * 初始化完成
     */
    private void initFinish() {
        activity.runOnUiThread(() -> {
            //避免回调两次出现回退异常,判断当前页面在栈顶才会跳转
            if(Rocket.isTopRocketStack(Frag_splash.class.getSimpleName())){
                if(!LoginHelper.getHasConfig(activity)){
                    //首次打开跳转到语言选择页面
                    toFrag(Frag_language.class, true,true,null);
                }else{
                    if(!LoginHelper.getHasLogin(activity)){
                        //上次没有登陆跳到登录页面
                        toFrag(Frag_login.class, true,true,null);
                    }else{
                        //上次登陆过跳到主页
                        toFrag(Frag_home.class, true,true,null);
                    }
                }
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        ndwTip.getTvContent().setText(R.string.app_new_version_detect);
        ndwTip.getTvCancel().setOnClickListener(v -> ndwTip.hide());
    }

    /**
     * 初始化Helper
     */
    @SuppressLint("SetTextI18n")
    private void initHelper() {
        systemUpdateHelper = new SystemUpdateHelper();
        //开始请求
        systemUpdateHelper.setOnPreRequestListener(() -> lwUpdateLoading.show(R.mipmap.img_data_upload_and_loading,getResources().getString(R.string.app_updating),true));
        //结束请求
        systemUpdateHelper.setOnFinishRequestListener(() -> lwUpdateLoading.hide());
        //请求失败
        systemUpdateHelper.setOnFailedRequestListener(object -> toast(object.toString(), 2000));
        //下载进度
        systemUpdateHelper.setOnDownloadProgressListener((checkType, progress) -> {
            if(lwUpdateLoading.getVisibility() != View.VISIBLE){
                lwUpdateLoading.show();
            }
            if(ndwTip.getVisibility() == View.VISIBLE){
                ndwTip.hide();
            }
            String content = NumberUtil.toFormate(progress);
            lwUpdateLoading.getTvLoading().setText(content+"%");
            if(progress >= 100){
                lwUpdateLoading.show(R.mipmap.img_update_completed,getResources().getString(R.string.app_update_completed),false);
                if(checkType == SystemUpdateHelper.CHECK_TYPE.FLASH){//下载完要更更新Flash
                    lwUpdateLoading.getTvLoading().setText(R.string.app_burning_module_firmware);
                    systemUpdateHelper.updateFlash();
                }else if(checkType == SystemUpdateHelper.CHECK_TYPE.MMY){//下载完要更新数据
                    lwUpdateLoading.getTvLoading().setText(R.string.app_updating_model_database);
                    systemUpdateHelper.updateMMY(activity);
                }else if(checkType == SystemUpdateHelper.CHECK_TYPE.SENSOR){
                    systemUpdateHelper.checkNextUpdate(activity,checkType);
                }
            }
        });
        //烧录进度
        systemUpdateHelper.setOnWriteFlashProgressListener((checkType, progress) -> {
            if(lwUpdateLoading.getVisibility() != View.VISIBLE){
                lwUpdateLoading.show();
            }
            if(ndwTip.getVisibility() == View.VISIBLE){
                ndwTip.hide();
            }
            String content = NumberUtil.toFormate(progress);
            lwUpdateLoading.getTvLoading().setText(getResources().getString(R.string.app_burning_module_firmware)+":" + content + "%");
            if(progress >= 100){
                lwUpdateLoading.show(R.mipmap.img_update_completed,getResources().getString(R.string.app_burning_succeeded),false);
                systemUpdateHelper.checkNextUpdate(activity,checkType);
            }
        });
        //检测版本更新
        systemUpdateHelper.setOnCheckUpdateListener((checkType, hasUpdate) -> {
            if(hasUpdate){
                if(lwUpdateLoading.getVisibility() == View.VISIBLE){
                    lwUpdateLoading.hide();
                }
                if(checkType == SystemUpdateHelper.CHECK_TYPE.FLASH){
                    ndwTip.getTvContent().setText(R.string.app_module_firmware_updated);
                }else if(checkType == SystemUpdateHelper.CHECK_TYPE.MMY){
                    ndwTip.getTvContent().setText(R.string.app_vehicle_payment_updated);
                }else if(checkType == SystemUpdateHelper.CHECK_TYPE.SENSOR){
                    ndwTip.getTvContent().setText(R.string.app_sendor_firmware_updated);
                }
                //点击确认就下载
                ndwTip.getTvOk().setOnClickListener(v -> {
                    ndwTip.hide();
                    if(checkType == SystemUpdateHelper.CHECK_TYPE.FLASH){
                        systemUpdateHelper.downloadFlash(activity);
                    }else if(checkType == SystemUpdateHelper.CHECK_TYPE.MMY){
                        systemUpdateHelper.downloadMMY(activity);
                    }else if(checkType == SystemUpdateHelper.CHECK_TYPE.SENSOR){
                        systemUpdateHelper.downloadSensor(activity);
                    }
                });
                //点击取消就检查下一个
                ndwTip.getTvCancel().setOnClickListener(v -> {
                    ndwTip.hide();
                    systemUpdateHelper.checkNextUpdate(activity,checkType);
                });
                ndwTip.getTvOk().setText(R.string.app_sure);
                ndwTip.setDoubleChoice();
                ndwTip.show();
            }else{
                systemUpdateHelper.checkNextUpdate(activity,checkType);
            }
        });
        //检测更新过程失败中断
        systemUpdateHelper.setOnUpdateProcessFailedListener((checkType, msg) -> {
            if(lwUpdateLoading.getVisibility() == View.VISIBLE){
                lwUpdateLoading.hide();
            }
            ndwTip.getTvContent().setText(msg);
            //点击确认就下载
            ndwTip.getTvOk().setOnClickListener(v -> {
                ndwTip.hide();
                systemUpdateHelper.checkNextUpdate(activity,checkType);
            });
            ndwTip.getTvOk().setText(R.string.app_ok);
            ndwTip.setSingleChoice();
            ndwTip.show();
        });
        //检测完毕
        systemUpdateHelper.setOnCheckUpdateFinishListener(this::initFinish);
        //烧录MMY
        systemUpdateHelper.setOnLoadMMYListener((checkType,stop, total, progress) -> {
            if(stop){
                lwUpdateLoading.show(R.mipmap.img_update_completed,getResources().getString(R.string.app_model_database_finished),false);
                systemUpdateHelper.checkNextUpdate(activity,checkType);
            }else{
                if(lwUpdateLoading.getVisibility() != View.VISIBLE){
                    lwUpdateLoading.show();
                }
                if(ndwTip.getVisibility() == View.VISIBLE){
                    ndwTip.hide();
                }
                String content = NumberUtil.toFormate((float) progress/total * 100);
                lwUpdateLoading.getTvLoading().setText(getResources().getString(R.string.app_writing_model_database)+":" + content + "%");
            }
        });
    }

    /* ************************** 接口回调 ************************** */

    /**
     * 初始化结果回调
     */
    private interface ResultCallback{
        void onSuccess();
    }
}
