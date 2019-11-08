package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.SelectAdapter;
import com.orange.tpms.helper.UnitHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_unit extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_temp)
    RecyclerView rvTemperature;//列表
    @BindView(R.id.rv_tire)
    RecyclerView rvTirePressure;//列表
    @BindView(R.id.rv_numeral)
    RecyclerView rvNumeral;//列表

    private SelectAdapter tempAdapter;//Adapter
    private SelectAdapter tireAdapter;//Adapter
    private SelectAdapter numeralAdapter;//Adapter
    private LinearLayoutManager tempLinearLayoutManager;//列表表格布局
    private LinearLayoutManager tireLinearLayoutManager;//列表表格布局
    private LinearLayoutManager numeralLinearLayoutManager;//列表表格布局
    private UnitHelper unitHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_unit;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onNexts(Object o) {

    }
    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_unit);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置TempRecyclerView
        if (tempLinearLayoutManager == null) {
            tempLinearLayoutManager = new LinearLayoutManager(activity);
        }
        rvTemperature.setLayoutManager(tempLinearLayoutManager);
        tempAdapter = new SelectAdapter(activity);
        rvTemperature.setAdapter(tempAdapter);
        tempAdapter.setOnItemClickListener((pos, content) ->{
            unitHelper.setTemp(activity,pos);
        });
        //配置TireRecyclerView
        if (tireLinearLayoutManager == null) {
            tireLinearLayoutManager = new LinearLayoutManager(activity);
        }
        rvTirePressure.setLayoutManager(tireLinearLayoutManager);
        tireAdapter = new SelectAdapter(activity);
        rvTirePressure.setAdapter(tireAdapter);
        tireAdapter.setOnItemClickListener((pos, content) ->{
            unitHelper.setPressure(activity,pos);
        });
        //配置NumeralRecyclerView
        if (numeralLinearLayoutManager == null) {
            numeralLinearLayoutManager = new LinearLayoutManager(activity);
        }
        rvNumeral.setLayoutManager(numeralLinearLayoutManager);
        numeralAdapter = new SelectAdapter(activity);
        rvNumeral.setAdapter(numeralAdapter);
        numeralAdapter.setOnItemClickListener((pos, content) ->{
            unitHelper.setNumeral(activity,pos);
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        unitHelper = new UnitHelper(activity);
        //开始请求
        unitHelper.setOnPreRequestListener(() -> {});
        //结束请求
        unitHelper.setOnFinishRequestListener(() -> {});
        //请求成功
        unitHelper.setOnSuccessRequestListener((object -> {}));
        //请求失败
        unitHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //温度单位列表回调
        unitHelper.setOnGetTempListener((select,arrayList) -> {
            tempAdapter.setItems(arrayList);
            tempAdapter.setSelect(select);
            tempAdapter.notifyDataSetChanged();
        });
        //压力单位列表回调
        unitHelper.setOnGetPressureListener((select,arrayList) -> {
            tireAdapter.setItems(arrayList);
            tireAdapter.setSelect(select);
            tireAdapter.notifyDataSetChanged();
        });
        //数字单位列表回调
        unitHelper.setOnGetNumeralListener((select,arrayList) -> {
            numeralAdapter.setItems(arrayList);
            numeralAdapter.setSelect(select);
            numeralAdapter.notifyDataSetChanged();
        });
        //读取单位列表
        unitHelper.getUnit(activity);
    }
}
