package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.SelectAdapter;
import com.orange.tpms.bean.LanguageBean;
import com.orange.tpms.helper.LanguageHelper;
import com.orange.tpms.widget.TitleWidget;

import java.util.ArrayList;
import java.util.List;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_language extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_language)
    RecyclerView rvLanguage;//列表

    private SelectAdapter languageAdapter;//Adapter
    private LinearLayoutManager linearLayoutManager;//列表表格布局
    private LanguageHelper languageHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_language;
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
        twTitle.setTvTitle(R.string.app_language_setting);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置TempRecyclerView
        if (linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(activity);
        }
        rvLanguage.setLayoutManager(linearLayoutManager);
        languageAdapter = new SelectAdapter(activity);
        rvLanguage.setAdapter(languageAdapter);
        languageAdapter.setOnItemClickListener((pos, content) ->{
            languageHelper.setLanguage(activity,pos);
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        languageHelper = new LanguageHelper(activity);
        //开始请求
        languageHelper.setOnPreRequestListener(() -> {});
        //结束请求
        languageHelper.setOnFinishRequestListener(() -> {});
        //请求成功
        languageHelper.setOnSuccessRequestListener((object -> {}));
        //请求失败
        languageHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //语言列表回调
        languageHelper.setOnGetlanguageListener((select,arrayList) -> {
            if(arrayList != null){
                List<String> languageList = new ArrayList<>();
                for(LanguageBean languageBean : arrayList){
                    languageList.add(languageBean.getName());
                }
                languageAdapter.setItems(languageList);
                languageAdapter.setSelect(select);
                languageAdapter.notifyDataSetChanged();
            }
        });
        //读取锁定列表
        languageHelper.getLanguage(activity);
    }
}
