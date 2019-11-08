package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.InformationAdapter;
import com.orange.tpms.helper.InformationHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_infomation extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_information)
    RecyclerView rvInformation;//列表

    private InformationAdapter informationAdapter;//Adapter
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private InformationHelper informationHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_infomation;
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
        twTitle.setTvTitle(R.string.app_device_information);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置TempRecyclerView
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvInformation.setLayoutManager(linearLayoutManager);
        informationAdapter = new InformationAdapter(activity);
        rvInformation.setAdapter(informationAdapter);
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        informationHelper = new InformationHelper();
        //开始请求
        informationHelper.setOnPreRequestListener(() -> {});
        //结束请求
        informationHelper.setOnFinishRequestListener(() -> {});
        //请求成功
        informationHelper.setOnSuccessRequestListener((object -> {}));
        //请求失败
        informationHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //锁定列表回调
        informationHelper.setOnGetInformationListener((arrayList) -> {
            informationAdapter.setItems(arrayList);
            informationAdapter.notifyDataSetChanged();
        });
        //读取锁定列表
        informationHelper.getInformation(activity);
    }
}
