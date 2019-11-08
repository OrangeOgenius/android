package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.Button;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import java.util.Locale;

/**
 * 隐私
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_policy extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_policy;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_disagree)
    private void disagree(View view){
        back();
    }

    @Event(R.id.bt_agree)
    private void agree(View view){
        toFrag( Frag_wifi.class,true,true, null);
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
