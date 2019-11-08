package com.orange.tpms.ue.frag;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.utils.WifiUtils;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * Setting
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_setting extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.tv_conneted_wifi)
    TextView tvConnectedWifi;//Wifi

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.tv_my_favourite)
    private void myFavourite(View view){
        toNextClass(Frag_setting_favourite.class);
    }

    @Event(R.id.tv_area)
    private void area(View view){
        toNextClass(Frag_setting_area.class);
    }

    @Event(R.id.tv_wifi)
    private void wifi(View view){
        toNextClass(Frag_setting_wifi.class);
    }

    @Event(R.id.tv_blue_bud)
    private void blueBud(View view){
        toNextClass(Frag_setting_bluebud.class);
    }

    @Event(R.id.tv_unit)
    private void unit(View view){
        toNextClass(Frag_setting_unit.class);
    }

    @Event(R.id.tv_auto_lock)
    private void autoLock(View view){
        toNextClass(Frag_setting_autolock.class);
    }

    @Event(R.id.tv_language)
    private void language(View view){
        toNextClass(Frag_setting_language.class);
    }

    @Event(R.id.tv_sounds)
    private void sounds(View view){
        toNextClass(Frag_setting_sounds.class);
    }

    @Event(R.id.tv_information)
    private void information(View view){
        toNextClass(Frag_setting_infomation.class);
    }

    @Event(R.id.tv_tips)
    private void tips(View view){
        toNextClass(Frag_setting_tips.class);
    }

    @Event(R.id.tv_system_reset)
    private void systemSeset(View view){
        toNextClass(Frag_setting_system_reset.class);
    }

    @Event(R.id.tv_system_update)
    private void systemUpdate(View view){
        toNextClass(Frag_setting_system_update.class);
    }

    @Event(R.id.tv_privacy_policy)
    private void privacyPolicy(View view){
        toNextClass(Frag_setting_policy.class);
    }

    /**
     * 页面跳转
     */
    private void toNextClass(Class nextClass){
        toFrag(nextClass,false,true,null);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_setting);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //设置已经连接的wifi
        String connetedWifi = WifiUtils.getInstance(activity).getConnectedSSID();
        if(TextUtils.isEmpty(connetedWifi)){
            tvConnectedWifi.setVisibility(View.GONE);
        }else{
            tvConnectedWifi.setVisibility(View.VISIBLE);
            tvConnectedWifi.setText(connetedWifi);
        }
    }
}
