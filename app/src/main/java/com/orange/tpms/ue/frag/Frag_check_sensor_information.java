package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.FunctionSelectAdapter;
import com.orange.tpms.bean.FunctionSelectBean;
import com.orange.tpms.widget.TitleWidget;

import bean.mmy.MMyBean;
import com.de.rocket.ue.injector.BindView;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensor Information
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_check_sensor_information extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.tv_content)
    TextView tvContent;//Title
    @BindView(R.id.b1)
    RelativeLayout b1;
    @BindView(R.id.b2)
    RelativeLayout b2;
    private MMyBean mMyBean = new MMyBean();

    @Override
    public int onInflateLayout() {
        return R.layout.frag_check_sensor_information;
    }

    @Override
    public void initViewFinish(View inflateView) {

    }

    @Override
    public void onNexts(Object o) {
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_sensor_info);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //初始化列表
        //配置RecyclerView
     b1.setOnClickListener(v -> {
         toFrag(Frag_check_sensor_read.class, false, true, "");
     });
        b2.setOnClickListener(v -> {
            toFrag(Frag_check_sensor_location.class, false, true, "");
        });
    }
}
