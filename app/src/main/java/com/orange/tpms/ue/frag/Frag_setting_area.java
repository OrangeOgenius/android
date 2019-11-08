package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.orange.tpms.R;
import com.orange.tpms.adapter.SelectAdapter;
import com.orange.tpms.helper.AreaHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 区域选择
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_area extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_autolock)
    RecyclerView rvAutoLock;//列表

    private SelectAdapter areaAdapter;//Adapter
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private AreaHelper areaHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_area;
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
        twTitle.setTvTitle(R.string.app_area);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置TempRecyclerView
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvAutoLock.setLayoutManager(linearLayoutManager);
        areaAdapter = new SelectAdapter(activity);
        rvAutoLock.setAdapter(areaAdapter);
        //单击选中
        areaAdapter.setOnItemClickListener((pos, content) ->{
            areaHelper.setArea(activity,pos);
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        areaHelper = new AreaHelper(activity);
        //开始请求
        areaHelper.setOnPreRequestListener(() -> {});
        //结束请求
        areaHelper.setOnFinishRequestListener(() -> {});
        //请求成功
        areaHelper.setOnSuccessRequestListener((object -> {}));
        //请求失败
        areaHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //锁定列表回调
        areaHelper.setOnGetAreaListener((select,arrayList) -> {
            areaAdapter.setItems(arrayList);
            areaAdapter.setSelect(select);
            areaAdapter.notifyDataSetChanged();
        });
        //读取锁定列表
        areaHelper.getArea(activity);
    }
}
