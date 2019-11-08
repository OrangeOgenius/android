package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.ImageView;

import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.SystemUpdateHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 系统升级页面
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_system_update extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.iv_check)
    ImageView ivCheck;//开关

    private SystemUpdateHelper systemUpdateHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_system_update;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initHelper();
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.iv_check)
    private void check(View view){
        ivCheck.setSelected(!ivCheck.isSelected());
        systemUpdateHelper.setIfSystemAutoUpdate(activity,ivCheck.isSelected());
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_system_update);
        //返回
        twTitle.setOnBackListener((view) -> onBackPresss());
        ivCheck.setSelected(systemUpdateHelper.getIfSystemAutoUpdate(activity));
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        systemUpdateHelper = new SystemUpdateHelper();
    }
}
