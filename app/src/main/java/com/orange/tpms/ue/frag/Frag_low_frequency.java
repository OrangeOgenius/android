package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;

import java.util.ArrayList;
import java.util.List;

import com.de.rocket.ue.injector.BindView;

/**
 * 低频设置
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_low_frequency extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.sp_number)
    Spinner spNumber;//Number

    @Override
    public int onInflateLayout() {
        return R.layout.frag_low_frequency;
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
        twTitle.setTvTitle(R.string.app_low_frequency);
        //返回
        twTitle.setOnBackListener((view) -> back());
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, list);
        spNumber.setAdapter(adapter);
    }
}
