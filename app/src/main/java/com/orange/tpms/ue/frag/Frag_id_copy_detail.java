package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.adapter.IDCopyDetailAdapter;
import com.orange.tpms.bean.IDCopyDetailBean;
import com.orange.tpms.bean.IDCopyFragBean;
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.helper.CopyIDHelper;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.ImageUtil;
import com.orange.tpms.utils.NumberUtil;
import com.orange.tpms.utils.VibMediaUtil;
import com.orange.tpms.widget.CarWidget;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import java.util.ArrayList;
import java.util.List;

import com.de.rocket.ue.injector.BindView;

/**
 * ID COPY 新页面
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_id_copy_detail extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_id_copy_neww)
    RecyclerView rvIDCopyDetail;
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.cw_car)
    CarWidget cwCar;//CarWidget

    private IDCopyDetailAdapter idCopyDetailAdapter;//适配器
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private CopyIDHelper copyIDHelper;
//    private IDCopyFragBean idCopyFragBean = new IDCopyFragBean();
    private VibMediaUtil vibMediaUtil;//音效与振动

    @Override
    public int onInflateLayout() {
        return R.layout.frag_id_copy_detail;
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
        updateView();
//        if (o instanceof IDCopyFragBean) {
//            idCopyFragBean = (IDCopyFragBean) o;
//            updateView(idCopyFragBean);
//        }
    }

    @Override
    public void onKeyTrigger(){
        program();
    }

    @Event(R.id.bt_program)
    private void program(View view) {
        program();
    }

    @Event(R.id.bt_menue)
    private void menue(View view){
        toFrag(Frag_home.class,true,true,null,true);
    }

    /**
     * 烧录
     */
    private void program(){
        vibMediaUtil.playVibrate();
        if (checkCanCopy()) {
            copyIDHelper.copyid();
        } else {
            toast(R.string.app_no_data_to_copy);
        }
    }

    /**
     * 刷新页面
     */
    private void updateView() {
        if ( PublicBean.SensorList.size() == PublicBean.NewSensorList.size()) {
            for (int i = 1; i < idCopyDetailAdapter.getItems().size(); i++) {
                if (i <= PublicBean.SensorList.size()) {
                    IDCopyDetailBean idCopyDetailBean = idCopyDetailAdapter.getItems().get(i);
                    idCopyDetailBean.setOriginalid(PublicBean.SensorList.get(i - 1));
                    idCopyDetailBean.setNewid(PublicBean.NewSensorList.get(i - 1));
                    idCopyDetailBean.setState(IDCopyDetailBean.STATE_NORMAL);
                    idCopyDetailAdapter.setItem(i, idCopyDetailBean);
                }
            }
            rvIDCopyDetail.setAdapter(idCopyDetailAdapter);
        }
    }

    /**
     * Copy成功
     */
    private void copySuccess(int index,boolean success) {
        if(success){
            cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_LEFT,CarWidget.CAR_STATUS.NORMAL);
        }else{
            cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_LEFT,CarWidget.CAR_STATUS.BAD);
        }
        if (PublicBean.SensorList.size() == PublicBean.NewSensorList.size()) {
            for (int i = 1; i < idCopyDetailAdapter.getItems().size(); i++) {
                if (i <= PublicBean.SensorList.size() && i == (index+1)) {
                    IDCopyDetailBean idCopyDetailBean = idCopyDetailAdapter.getItems().get(i);
                    if(success){
                        idCopyDetailBean.setState(IDCopyDetailBean.STATE_SUCCESS);
                    }else{
                        idCopyDetailBean.setState(IDCopyDetailBean.STATE_FAILED);
                    }
                    idCopyDetailAdapter.setItem(i, idCopyDetailBean);
                }
            }
            rvIDCopyDetail.setAdapter(idCopyDetailAdapter);
        }
    }

    /**
     * 检测是否可以copy数据了
     */
    private boolean checkCanCopy() {
        for (int i = 1; i < idCopyDetailAdapter.getItems().size(); i++) {
            IDCopyDetailBean idCopyDetailBean = idCopyDetailAdapter.getItems().get(i);
            //只要有一个不为空即可
            if (!TextUtils.isEmpty(idCopyDetailBean.getOriginalid())
                    && !TextUtils.isEmpty(idCopyDetailBean.getNewid())) {
                return true;
            }
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
        twTitle.setTvTitle(R.string.app_copy_new);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvIDCopyDetail.setLayoutManager(linearLayoutManager);
        idCopyDetailAdapter = new IDCopyDetailAdapter(activity);
        rvIDCopyDetail.setAdapter(idCopyDetailAdapter);
        //数据源
        List<IDCopyDetailBean> numberList = new ArrayList<>();
        IDCopyDetailBean titleBean = new IDCopyDetailBean("", getString(R.string.app_original_id), getString(R.string.app_new_sensor), getString(R.string.app_check_up), IDCopyDetailBean.STATE_HIDE);
        IDCopyDetailBean frBean = new IDCopyDetailBean("FR", "", "", "", IDCopyDetailBean.STATE_HIDE);
        IDCopyDetailBean rrBean = new IDCopyDetailBean("RR", "", "", "", IDCopyDetailBean.STATE_HIDE);
        IDCopyDetailBean rlBean = new IDCopyDetailBean("RL", "", "", "", IDCopyDetailBean.STATE_HIDE);
        IDCopyDetailBean flBean = new IDCopyDetailBean("FL", "", "", "", IDCopyDetailBean.STATE_HIDE);
        numberList.add(titleBean);
        numberList.add(frBean);
        numberList.add(rrBean);
        numberList.add(rlBean);
        numberList.add(flBean);
        idCopyDetailAdapter.setItems(numberList);
        idCopyDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        copyIDHelper = new CopyIDHelper();
        //开始请求
        copyIDHelper.setOnPreRequestListener(() -> lwLoading.show());
        //结束请求
        copyIDHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //请求失败
        copyIDHelper.setOnFailedRequestListener((object -> toast(object.toString(), 2000)));
        //进度
        copyIDHelper.setOnProgressListener(progress -> {
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show();
            }
            String content = NumberUtil.toFormate(progress);
            lwLoading.getTvLoading().setText(String.valueOf(content + "%"));
        });
        //IDCopy成功
        copyIDHelper.setOnIDCopySuccessListener((index,success) -> {
            Log.v("yhd-","setOnIDCopySuccessListener:"+" index:"+index+" success:"+success);
            vibMediaUtil.playBeep();
            //btProgram.setText("Pre-Program");
            copySuccess(index,success);
        });
    }
}
