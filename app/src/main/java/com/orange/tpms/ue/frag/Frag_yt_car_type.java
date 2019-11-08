package com.orange.tpms.ue.frag;

import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.bean.YTSelectModelBean;
import com.orange.tpms.widget.TitleWidget;
import com.de.rocket.ue.injector.BindView;


/**
 * 启动页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_yt_car_type extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_yt_car_type;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.iv_normal)
    private void onNormalSelect(View view){
        toFrag(Frag_yt_select_model.class,false,true,YTSelectModelBean.TYPR_NORMAL);
    }

    @Event(R.id.iv_high)
    private void onHighSelect(View view){
        toFrag(Frag_yt_select_model.class,false,true,YTSelectModelBean.TYPR_HIGH);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题栏
        twTitle.setTvTitle(R.string.app_orange_tpms);
        twTitle.setBgColor(R.color.color_yt);
        twTitle.setOnBackListener((view) -> back());
    }
}
