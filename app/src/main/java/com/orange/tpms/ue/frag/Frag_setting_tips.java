package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.ImageView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_tips extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_tips;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.iv_check)
    private void check(View view){
        view.setSelected(!view.isSelected());
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_tips_notifications);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }
}
