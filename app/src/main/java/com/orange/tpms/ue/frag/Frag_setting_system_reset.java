package com.orange.tpms.ue.frag;

import android.view.View;

import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.LoginHelper;
import com.orange.tpms.helper.SystemUpdateHelper;
import com.orange.tpms.widget.ResetWidget;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 系统重置页面
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_system_reset extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rsw_reset)
    ResetWidget rswReset;//Reset

    private SystemUpdateHelper systemUpdateHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_system_reset;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_reset)
    private void reset(View view){
        rswReset.show();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_system_reset);
        //返回
        twTitle.setOnBackListener(view -> back());
        //恢复出厂设置
        rswReset.setOnResetClickListener(v -> {
            rswReset.hide();
            systemUpdateHelper.systemReset();
            logout();
        });
    }

    /**
     * 初始化Helper
     */
    private void logout() {
        LoginHelper loginHelper = new LoginHelper();
        //退出登陆成功
        loginHelper.setOnLogoutSuccessListener((() -> toFrag(Frag_login.class,true,true,null,true)));
        //退出登录
        loginHelper.logout(activity);
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        systemUpdateHelper = new SystemUpdateHelper();
    }
}
