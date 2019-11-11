package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.adapter.IDCopyAdapter;
import com.orange.tpms.bean.*;
import com.orange.tpms.helper.CopyIDHelper;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.NumberUtil;
import com.orange.tpms.utils.VibMediaUtil;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.ScanWidget;
import com.orange.tpms.widget.SensorWayWidget;
import com.orange.tpms.widget.TitleWidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.de.rocket.ue.injector.BindView;

import bean.mmy.MMyBean;

/**
 * ID COPY 详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_id_copy_original extends Frag_base {
    String ObdHex="00";
    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_id_copy)
    RecyclerView rvIDCopy;//IDCopy
    @BindView(R.id.tv_content)
    TextView tvContent;//title
    @BindView(R.id.sww_select)
    SensorWayWidget swwSelect;
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
//    @BindView(R.id.scw_tips)
//    ScanWidget scwTips;//Tips

    private IDCopyAdapter idCopyAdapter;//适配器
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private CopyIDHelper copyIDHelper;
    private HardwareApp.DataReceiver dataReceiver;
    private VibMediaUtil vibMediaUtil;//音效与振动

    @Override
    public int onInflateLayout() {
        return R.layout.frag_id_copy_original;
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
//        ObdHex=((MainActivity)activity).itemDAO.GetHex(PublicBean.SelectMake, PublicBean.SelectModel,PublicBean.SelectYear);
        tvContent.setText(ObdHex);
    }

    @Override
    public void onKeyTrigger(){
        if(swwSelect.isShown()){
            swwSelect.pllTrigger.performClick();
        }

        //避免回调两次出现回退异常,判断当前页面在栈顶才会读取
        if(Rocket.isTopRocketStack(Frag_id_copy_original.class.getSimpleName())){
            if (swwSelect.getSelectType() == SensorWayWidget.SELECT_TYPE_TRIGGER) {
                copyIDHelper.trigger(ObdHex);
            }
        }
    }

    @Override
    public void onKeyScan(){
        Rocket.writeOuterLog("Frag_id_copy_original::onKeyScan");
        if(swwSelect.isShown()){
            swwSelect.pllScan.performClick();
        }

        //避免回调两次出现回退异常,判断当前页面在栈顶才会读取
        if(Rocket.isTopRocketStack(Frag_id_copy_original.class.getSimpleName())){
            Rocket.writeOuterLog("Frag_id_copy_original::onKeyScan->isTopRocketStack");
            if (swwSelect.getSelectType() == SensorWayWidget.SELECT_TYPE_SCAN) {
                Rocket.writeOuterLog("Frag_id_copy_original::onKeyScan->SelectType:SELECT_TYPE_SCAN");
                HardwareApp.getInstance().scan();
                lwLoading.show(getResources().getString(R.string.app_scaning));
            }else{
                Rocket.writeOuterLog("Frag_id_copy_original::onKeyScan->SelectType:"+swwSelect.getSelectType());
            }
        }
    }

    @Override
    public boolean onBackPresss(){
        return true;
    }

    @Event(R.id.bt_new_sensor_list)
    private void newSensor(View view){
        vibMediaUtil.playVibrate();
        if(checkHasSensor()){
            if(!haveSameSensorid()){
                //主动释放,不然会因为生命周期的先后顺序导致注册解绑冲突
                HardwareApp.getInstance().switchScan(false);
                HardwareApp.getInstance().removeDataReceiver(dataReceiver);
                //传递参数
//                IDCopyFragBean idCopyFragBean = new IDCopyFragBean();
//                idCopyFragBean.setOrignalSendorid(getSensoridList());
//                idCopyFragBean.setmMyBean(mMyBean);
                PublicBean.SensorList=getSensoridList();
                toFrag(Frag_id_copy_new.class, true,true,"");
            }else{
                toast(R.string.app_sensor_repeated);
            }
        }else{
            toast(R.string.app_no_sensor_set);
        }
    }

    @Event(R.id.bt_menue)
    private void menue(View view){
        toFrag(Frag_home.class,true,true,null,true);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        swwSelect.setTitle(getResources().getString(R.string.app_original_sensor));
        //音效与震动
        vibMediaUtil = new VibMediaUtil(activity);
        //设置标题
        twTitle.setTvTitle(R.string.app_copy_original);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvIDCopy.setLayoutManager(linearLayoutManager);
        idCopyAdapter = new IDCopyAdapter(activity);
        rvIDCopy.setAdapter(idCopyAdapter);
        //数据源
        List<IDCopyBean> numberList = new ArrayList<>();
        IDCopyBean titleBean = new IDCopyBean("",getString(R.string.app_id_clear),getString(R.string.app_psi),getString(R.string.app_temp),getString(R.string.app_bat_clear),false);
        IDCopyBean frBean = new IDCopyBean("FR","","","","",false);
        IDCopyBean rrBean = new IDCopyBean("RR","","","","",false);
        IDCopyBean rlBean = new IDCopyBean("RL","","","","",false);
        IDCopyBean flBean = new IDCopyBean("FL","","","","",false);
        numberList.add(titleBean);
        numberList.add(frBean);
        numberList.add(rrBean);
        numberList.add(rlBean);
        numberList.add(flBean);
        idCopyAdapter.setItems(numberList);
        idCopyAdapter.notifyDataSetChanged();
        //选择方式
        swwSelect.setOnKeyinClickListener(() -> updateEditable());
        //硬件
        HardwareApp.getInstance().switchScan(true);
        dataReceiver = new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {
                Rocket.writeOuterLog("Frag_id_copy_original::scanMsgReceive->content:"+content);
                lwLoading.hide();
                if(!content.contains(":") && !content.contains("*")){
                    if(!content.equals("nofound")){
                        toast(R.string.app_invalid_sensor_qrcode);
                    }else{
                        toast(R.string.app_scan_code_timeout);
                    }
                    return;
                }
                String sensorid;
                if(content.contains("**")){
                    sensorid = MMYQrCodeBean.toQRcodeBean(content).getMmyNumber();
                }else{
                    sensorid = SensorQrCodeBean.toQRcodeBean(content).getSensorID();
                }
                if(TextUtils.isEmpty(sensorid)){
                    toast(R.string.app_invalid_sensor_qrcode);
                    return;
                }
                Rocket.writeOuterLog("Frag_id_copy_new::scanMsgReceive->sensorid:"+sensorid);
                vibMediaUtil.playBeep();
                if(!haveSameSensorid(sensorid)){
                    updateSensorid(sensorid);
                }else{
                    toast(R.string.app_sensor_repeated);
                }
            }

            @Override
            public void uart2MsgReceive(String content) {

            }
        };
        HardwareApp.getInstance().addDataReceiver(dataReceiver);
    }

    /**
     * 插入传感器id
     */
    private void updateSensorid(String sensorid) {
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            //sensorid为空才插入
            if(TextUtils.isEmpty(idCopyBean.getSensorid())){
                idCopyBean.setSensorid(sensorid);
                idCopyAdapter.setItem(i,idCopyBean);
                rvIDCopy.setAdapter(idCopyAdapter);
                checkSelectFinish();
                return;
            }
        }
    }

    /**
     * 插入传感器id
     */
    private void updateSensorid(String sensorid,String psi,String temp,String bat) {
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            //sensorid为空才插入
            if(TextUtils.isEmpty(idCopyBean.getSensorid())){
                idCopyBean.setSensorid(sensorid);
                idCopyBean.setPsi(psi);
                idCopyBean.setTemp(temp);
                idCopyBean.setBat(bat);
                idCopyAdapter.setItem(i,idCopyBean);
                rvIDCopy.setAdapter(idCopyAdapter);
                checkSelectFinish();
                return;
            }
        }
    }

    /**
     * 刷新是否能够编辑的状态
     */
    private void updateEditable() {
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            idCopyBean.setEditable(true);
            idCopyAdapter.setItem(i,idCopyBean);
        }
        rvIDCopy.setAdapter(idCopyAdapter);
    }

    /**
     * 检测是否完成选择
     */
    private boolean checkSelectFinish() {
        boolean finish = true;
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            //sensorid都不为空才行
            if(TextUtils.isEmpty(idCopyBean.getSensorid())){
                finish = false;
            }
        }
        return finish;
    }

    /**
     * 获取传感器id列表
     */
    private List<String> getSensoridList() {
        List<String> sensorList = new ArrayList<>();
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            //sensorid都不为空才行
            if(!TextUtils.isEmpty(idCopyBean.getSensorid())){
                sensorList.add(idCopyBean.getSensorid());
            }
        }
        return sensorList;
    }

    /**
     * 检测是否有数据了
     */
    private boolean checkHasSensor() {
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            //只要有一个不为空即可
            if(!TextUtils.isEmpty(idCopyBean.getSensorid())){
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否有重复的
     */
    private boolean haveSameSensorid(String sensorid) {
        for (int i = 1; i < idCopyAdapter.getItems().size(); i++) {
            IDCopyBean idCopyBean = idCopyAdapter.getItems().get(i);
            if(!TextUtils.isEmpty(sensorid) && !TextUtils.isEmpty(idCopyBean.getSensorid())){
                if(sensorid.equals(idCopyBean.getSensorid())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测是否有重复的
     */
    private boolean haveSameSensorid() {
        Set<String> set = new HashSet<>();
        Set<String> exist = new HashSet<>();
        for (IDCopyBean idCopyBean : idCopyAdapter.getItems()) {
            String sensorid = idCopyBean.getSensorid();
            if(!TextUtils.isEmpty(sensorid)){
                if (set.contains(sensorid)) {
                    exist.add(sensorid);
                } else {
                    set.add(sensorid);
                }
            }
        }
        return !exist.isEmpty();
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        copyIDHelper = new CopyIDHelper();
        //开始请求
        copyIDHelper.setOnPreRequestListener(() -> lwLoading.show(getResources().getString(R.string.app_data_reading)));
        //结束请求
        copyIDHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //请求失败
        copyIDHelper.setOnFailedRequestListener((object -> toast(object.toString(), 2000)));
        //读取传感器情况
        copyIDHelper.setOnStrggerSuccessListener((sensorid, psi, temp, bat) -> {
            vibMediaUtil.playBeep();
            if(!haveSameSensorid(sensorid)){
                updateSensorid(sensorid,psi,temp,bat);
            }else{
                toast(R.string.app_sensor_repeated);
            }
        });
        //进度
        copyIDHelper.setOnProgressListener(progress -> {
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show();
            }
            String content = NumberUtil.toFormate(progress);
            lwLoading.getTvLoading().setText(String.valueOf(content + "%"));
        });
    }
}
