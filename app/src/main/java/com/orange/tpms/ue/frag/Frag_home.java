package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.adapter.FunctionSelectAdapter;
import com.orange.tpms.bean.FunctionSelectBean;
import com.orange.tpms.utils.PackageUtils;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_home extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_function)
    RecyclerView rvFunction;
    @BindView(R.id.tv_orange_co)
    TextView tvOrangeCo;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_home;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    /**
     * 初始化页面
     */
    private void initView() {
        //显示版本号
        tvOrangeCo.setText(String.valueOf(getResources().getString(R.string.app_orange_co)+"-"+PackageUtils.getVersionName(activity)));
        //设置标题
        twTitle.setTvTitle(R.string.app_o_genius);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //初始化列表
        //配置RecyclerView
        rvFunction.setLayoutManager(new GridLayoutManager(activity, 2));
        FunctionSelectAdapter functionSelectAdapter = new FunctionSelectAdapter(activity);
        functionSelectAdapter.setItems(getFunctionList());
        rvFunction.setAdapter(functionSelectAdapter);
        //点击事件
        functionSelectAdapter.setOnItemClickListener((index, functionSelectBean) -> toFrag(functionSelectBean.getTargetClass()));
    }

    /**
     * 功能列表
     */
    private List<FunctionSelectBean> getFunctionList(){
        //列表信息
        List<FunctionSelectBean> funcList = new ArrayList<>();
        funcList.add(new FunctionSelectBean(R.drawable.bt_check_sensor_selecter,true,getString(R.string.app_home_check_sensor),Frag_check_sensor.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_program_sensor_selecter,true,getString(R.string.app_home_program_sensor_phone),Frag_program_sensor.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_id_copy_selecter,true,getString(R.string.app_home_id_copy),Frag_id_copy.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_obdii_relearn_selecter,false,getString(R.string.app_home_obdii_relearn),Frag_setting.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_usb_pad_selecter,false,getString(R.string.app_usb_pad),Frag_setting.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_setting_selecter,true,getString(R.string.app_setting),Frag_setting.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_cloud_information_selecter,false,getString(R.string.app_home_cloud_information),Frag_setting.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_online_shopping_selecter,false,getString(R.string.app_home_online_shopping),Frag_setting.class));
        funcList.add(new FunctionSelectBean(R.drawable.bt_users_manual_selecter,false,getString(R.string.app_user_manual),Frag_setting.class));
        return funcList;
    }
}
