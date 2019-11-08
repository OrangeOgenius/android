package com.orange.tpms.ue.frag;

import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;

import java.util.Locale;

import com.de.rocket.ue.injector.BindView;

/**
 * 语言选择页面
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_language extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_language;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_select_cn)
    private void selectCN(View view){
        gotoPolicy(Locale.CHINA);
    }

    @Event(R.id.bt_select_en)
    private void selectEN(View view){
        gotoPolicy(Locale.ENGLISH);
    }

    @Event(R.id.bt_select_deu)
    private void selectDEU(View view){
        gotoPolicy(Locale.GERMAN);
    }

    @Event(R.id.bt_select_ita)
    private void selectITA(View view){
        gotoPolicy(Locale.ITALIAN);
    }

    @Event(R.id.bt_select_tr)
    private void selectTW(View view){
        gotoPolicy(Locale.TAIWAN);
    }

    /**
     * 前往Wifi页面
     * @param locale 选择语言
     */
    private void gotoPolicy(Locale locale) {
        setSaveLocale(locale);
        toFrag(Frag_policy.class, true,true,null);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_language);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }
}
