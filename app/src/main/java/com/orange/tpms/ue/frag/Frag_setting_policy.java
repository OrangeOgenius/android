package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;
import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_policy extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_policy;
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
        //设置标题
        twTitle.setTvTitle(R.string.app_privacy_policy);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }
}
