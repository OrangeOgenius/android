package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.SelectAdapter;
import com.orange.tpms.helper.AutoLockHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_autolock extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_autolock)
    RecyclerView rvAutoLock;//列表

    private SelectAdapter autoLockAdapter;//Adapter
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private AutoLockHelper autoLockHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_autolock;
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
        twTitle.setTvTitle(R.string.app_sleep);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置TempRecyclerView
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvAutoLock.setLayoutManager(linearLayoutManager);
        autoLockAdapter = new SelectAdapter(activity);
        rvAutoLock.setAdapter(autoLockAdapter);
        //单击选中
        autoLockAdapter.setOnItemClickListener((pos, content) ->{
            autoLockHelper.setAutoLock(activity,pos);
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        autoLockHelper = new AutoLockHelper(activity);
        //开始请求
        autoLockHelper.setOnPreRequestListener(() -> {});
        //结束请求
        autoLockHelper.setOnFinishRequestListener(() -> {});
        //请求成功
        autoLockHelper.setOnSuccessRequestListener((object -> {}));
        //请求失败
        autoLockHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //锁定列表回调
        autoLockHelper.setOnGetAutoLockListener((select,arrayList) -> {
            autoLockAdapter.setItems(arrayList);
            autoLockAdapter.setSelect(select);
            autoLockAdapter.notifyDataSetChanged();
        });
        //读取锁定列表
        autoLockHelper.getAutoLock(activity);
    }
}
