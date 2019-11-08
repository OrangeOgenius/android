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
import com.orange.tpms.adapter.ProgramAdapter;
import com.orange.tpms.bean.MMYQrCodeBean;
import com.orange.tpms.bean.ProgramFragBean;
import com.orange.tpms.bean.ProgramItemBean;
import com.orange.tpms.bean.ScanQrCodeBean;
import com.orange.tpms.bean.SensorQrCodeBean;
import com.orange.tpms.helper.ProgramSensorHelper;
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

import bean.hardware.SensorDataBean;
import bean.mmy.MMyBean;

import com.de.rocket.ue.injector.BindView;

/**
 * 烧录详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_program_detail extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_program)
    RecyclerView rvProgram;
    @BindView(R.id.sww_select)
    SensorWayWidget swwSelect;
    @BindView(R.id.tv_program_title)
    TextView tvTitle;
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.bt_program)
    Button btProgram;
    @BindView(R.id.scw_tips)
    ScanWidget scwTips;//Tips
    String ObdHex="00";
    private ProgramAdapter programAdapter;//适配器
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private ProgramSensorHelper programSensorHelper;//Helper
    private List<ProgramItemBean> numberList = new ArrayList<>();
//    private ProgramFragBean programFragBean = new ProgramFragBean(1);//ProgramFragBean
    private HardwareApp.DataReceiver dataReceiver;
    private VibMediaUtil vibMediaUtil;//音效与振动

    @Override
    public int onInflateLayout() {
        return R.layout.frag_program_detail;
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
        HardwareApp.getInstance().switchScan(false);
        HardwareApp.getInstance().removeDataReceiver(dataReceiver);
    }

    @Override
    public void onNexts(Object o) {
        ObdHex=((MainActivity)activity).itemDAO.GetHex(MainActivity.SelectMake,MainActivity.SelectModel,MainActivity.SelectYear);
//        if (o instanceof ProgramFragBean) {
//            programFragBean = (ProgramFragBean) o;
//            MMyBean mMyBean = programFragBean.getmMyBean();
//            tvTitle.setText(mMyBean.getHex());
        updateList(MainActivity.ProgramNumber);
//        }
    }

    @Override
    public void onKeyTrigger(){
        if(swwSelect.isShown()){
            swwSelect.pllTrigger.performClick();
        }
        if(scwTips.isShown()){
            scwTips.hide();
        }
        //避免回调两次出现回退异常,判断当前页面在栈顶才会读取
        if(Rocket.isTopRocketStack(Frag_program_detail.class.getSimpleName())){
            if (swwSelect.getSelectType() == SensorWayWidget.SELECT_TYPE_TRIGGER) {
                programSensorHelper.trigger(ObdHex);
            }
        }
    }

    @Override
    public void onKeyScan(){
        Rocket.writeOuterLog("Frag_program_detail::onKeyScan");
        if(swwSelect.isShown()){
            swwSelect.pllScan.performClick();
        }
        if(scwTips.isShown()){
            scwTips.hide();
        }
        //避免回调两次出现回退异常,判断当前页面在栈顶才会读取
        if(Rocket.isTopRocketStack(Frag_program_detail.class.getSimpleName())){
            Rocket.writeOuterLog("Frag_program_detail::onKeyScan->isTopRocketStack");
            if (swwSelect.getSelectType() == SensorWayWidget.SELECT_TYPE_SCAN) {
                Rocket.writeOuterLog("Frag_program_detail::onKeyScan->SELECT_TYPE_SCAN");
                HardwareApp.getInstance().scan();
                lwLoading.show(getResources().getString(R.string.app_scaning));
            }else{
                Rocket.writeOuterLog("Frag_program_detail::onKeyScan->SelectType:"+swwSelect.getSelectType());
            }
        }
    }

    @Event(R.id.bt_program)
    private void program(View view) {
        program();
    }

    @Event(R.id.bt_menue)
    private void menue(View view){
        toFrag(Frag_home.class,true,true,null,true);
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

    /**
     * 烧录
     */
    private void program(){
        vibMediaUtil.playVibrate();
        if(checkSelectFinish()){
            if(!haveSameSensorid()){
                programSensorHelper.writeSensor(MainActivity.ProgramNumber,ObdHex,((MainActivity)activity).itemDAO.getMMY(MainActivity.SelectMake,MainActivity.SelectModel,MainActivity.SelectYear));
            }else{
                toast(R.string.app_duplicate_items);
            }
        }else{
            toast(R.string.app_fillin_all_sensor_id);
        }
    }

    /**
     * 刷新是否能够编辑的状态
     */
    private void updateEditable() {
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                programItemBean.setEditable(true);
                programAdapter.setItem(i,programItemBean);
            }
            rvProgram.setAdapter(programAdapter);
        }
    }

    /**
     * 插入传感器id
     */
    private void updateSensorid(String sensorid) {
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                //sensorid为空才插入
                if(TextUtils.isEmpty(programItemBean.getSensorid())){
                    programItemBean.setShowIndex(true);
                    programItemBean.setSensorid(sensorid);
                    programItemBean.setState(ProgramItemBean.STATE_NORMAL);
                    programAdapter.setItem(i,programItemBean);
                    rvProgram.setAdapter(programAdapter);
                    checkSelectFinish();
                    return;
                }
            }
        }
    }

    /**
     * 更新列表
     */
    private void updateList(int number) {
        //数目要对上
        if (programAdapter.getItems().size() >= number && number > 0) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                if(i > number - 1){
                    return;
                }
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                programItemBean.setShowIndex(true);
                programItemBean.setState(ProgramItemBean.STATE_NORMAL);
                programAdapter.setItem(i,programItemBean);
                rvProgram.setAdapter(programAdapter);
            }
        }
    }

    /**
     * 设置更新状态
     */
    private void updateProgramState(List<SensorDataBean> sensorDataBeans) {
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                if(sensorDataBeans == null || sensorDataBeans.size() == 0){
                    programItemBean.setState(ProgramItemBean.STATE_FAILED);
                }else{
                    //原始ID
                    String originSensorid = programItemBean.getSensorid();
                    if(!TextUtils.isEmpty(originSensorid)){
                        boolean exist = false;
                        for(SensorDataBean sensorDataBean : sensorDataBeans){
                            Log.d("sensorDataBean",""+sensorDataBean.getId_len());
                            if(!TextUtils.isEmpty(sensorDataBean.getSensor_id())){
                                if(originSensorid.equals(sensorDataBean.getSensor_id())){
                                    exist = true;
                                }
                            }
                        }
                        if(exist){
                            programItemBean.setState(ProgramItemBean.STATE_SUCCESS);
                        }else{
                            programItemBean.setState(ProgramItemBean.STATE_FAILED);
                        }
                    }

                }
                programAdapter.setItem(i,programItemBean);
            }
            rvProgram.setAdapter(programAdapter);
        }
    }

    /**
     * 更新状态
     */
    private void updateProgramState(String sensorid,int state) {
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                if(!TextUtils.isEmpty(sensorid) && !TextUtils.isEmpty(programItemBean.getSensorid())){
                    if(sensorid.equals(programItemBean.getSensorid())){
                        Log.e("sensorid",sensorid);
                        programItemBean.setState(state);
                    }
                }
                if(TextUtils.isEmpty(sensorid)){
                    programItemBean.setState(state);
                }
                programAdapter.setItem(i,programItemBean);
            }
            rvProgram.setAdapter(programAdapter);
        }
    }

    /**
     * 检测是否完成选择
     */
    private boolean checkSelectFinish() {
        boolean finish = true;
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                //sensorid都不为空才行
                if(TextUtils.isEmpty(programItemBean.getSensorid())){
                    finish = false;
                }
            }
        }
        return finish;
    }

    /**
     * 检测是否有重复的
     */
    private boolean haveSameSensorid(String sensorid) {
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber && !TextUtils.isEmpty(sensorid)) {
            for (int i = 0; i < MainActivity.ProgramNumber; i++) {
                ProgramItemBean programItemBean = programAdapter.getItems().get(i);
                //sensorid都不为空才行
                if(sensorid.equals(programItemBean.getSensorid())){
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
        //数目要对上
        if (programAdapter.getItems().size() >= MainActivity.ProgramNumber) {
            Set<String> set = new HashSet<>();
            Set<String> exist = new HashSet<>();
            for (ProgramItemBean programItemBean : programAdapter.getItems()) {
                String sensorid = programItemBean.getSensorid();
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
        return false;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //音效与震动
        vibMediaUtil = new VibMediaUtil(activity);
        //设置标题
        twTitle.setTvTitle(R.string.app_home_program_sensor_phone);
        //返回
        twTitle.setOnBackListener((view) -> onBackPresss());
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvProgram.setLayoutManager(linearLayoutManager);
        programAdapter = new ProgramAdapter(activity);
        rvProgram.setAdapter(programAdapter);
        //数据源
        for (int i = 0; i < 12; i++) {
            numberList.add(new ProgramItemBean(false,"", ProgramItemBean.STATE_HIDE, false));
        }
        programAdapter.setItems(numberList);
        //选择方式
        swwSelect.setOnScanClickListener(() -> scwTips.show());
        swwSelect.setOnTriggerClickListener(() -> scwTips.show());
        swwSelect.setOnKeyinClickListener(this::updateEditable);
        HardwareApp.getInstance().switchScan(true);
        dataReceiver = new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {
                Rocket.writeOuterLog("Frag_program_detail::scanMsgReceive->content:"+content);
                lwLoading.hide();
                Log.v("yhd-","content:"+content);
                //兼容三种
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
     * 初始化Helper
     */
    private void initHelper() {
        programSensorHelper = new ProgramSensorHelper();
        //开始请求
        programSensorHelper.setOnPreRequestListener(() -> lwLoading.show(getResources().getString(R.string.app_loading_data)));
        //结束请求
        programSensorHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //请求失败
        programSensorHelper.setOnFailedRequestListener((object -> toast(object.toString(), 2000)));
        //读取传感器情况
        programSensorHelper.setOnStrggerSuccessListener(sensorid -> {
            Log.v("yhd-","setOnStrggerSuccessListener:"+"sensorid:"+sensorid);
            vibMediaUtil.playBeep();
            if(!haveSameSensorid(sensorid)){
                updateSensorid(sensorid);
            }else{
                toast(R.string.app_sensor_repeated);
            }
        });
        //进度
        programSensorHelper.setOnProgressListener(progress -> {
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show();
            }
            String content = NumberUtil.toFormate(progress);
            lwLoading.getTvLoading().setText(content + "%");
        });
        //烧录成功
        programSensorHelper.setOnProgramSuccessListener((sensorid,success) -> {
            Log.v("yhd-","setOnProgramSuccessListener:"+"sensorid:"+sensorid+" success:"+success);
            vibMediaUtil.playBeep();
            btProgram.setText(R.string.app_re_program);
            if(success){
                updateProgramState(sensorid,ProgramItemBean.STATE_SUCCESS);
            }else{
                updateProgramState(sensorid,ProgramItemBean.STATE_FAILED);
            }
        });
        //开始检测
        programSensorHelper.setOnCheckProgramListener(() -> {
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show();
            }
            lwLoading.getTvLoading().setText(R.string.app_checking);
        });
        //检测超时
        programSensorHelper.setOnCheckTimeoutListener(sensorDataBeans -> {
            Log.v("yhd-","setOnCheckTimeoutListener:"+"sensorDataBeans:"+sensorDataBeans.size());
            vibMediaUtil.playBeep();
            updateProgramState(sensorDataBeans);
        });
    }
}
