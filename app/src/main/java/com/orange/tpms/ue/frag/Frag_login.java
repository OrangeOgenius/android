package com.orange.tpms.ue.frag;

import android.view.View;
import android.widget.EditText;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.LoginHelper;
import com.orange.tpms.utils.OggUtils;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 登录页面
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_login extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.et_login_password)
    EditText etLoginPassword;//密码
    @BindView(R.id.et_login_name)
    EditText etLoginName;//用户名
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading

    private LoginHelper loginHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_login;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initHelper();
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_login)
    private void login(View view){//登陆
        if(!OggUtils.isEmpty(etLoginName) && !OggUtils.isEmpty(etLoginPassword)){
            loginHelper.login(activity,etLoginName.getText().toString(),etLoginPassword.getText().toString());
        }else{
            toast(R.string.app_content_empty,2000);
        }
    }

    @Event(R.id.tv_forget)
    private void forget(View view){//忘记密码
        toFrag(Frag_reset.class,false,true,null);
    }

    @Event(R.id.bt_register)
    private void register(View view){//注册
        toFrag(Frag_register.class,false,true,null);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //进入登录界面代表配置已经完成
        LoginHelper.setHasConfig(activity,true);
        //设置账号密码
        etLoginName.setText(LoginHelper.getAccount(activity));
        etLoginPassword.setText(LoginHelper.getPassword(activity));
        //设置Loading提示
        lwLoading.getTvLoading().setText(R.string.app_loading);
        //设置标题
        twTitle.setTvTitle(R.string.app_orange_tpms);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        loginHelper = new LoginHelper();
        //开始请求
        loginHelper.setOnPreRequestListener(() -> lwLoading.show(getResources().getString(R.string.app_sign_ing)));
        //结束请求
        loginHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //登陆成功
        loginHelper.setOnLoginSuccessListener((() -> {
            toFrag(Frag_home.class,true,true,null);
        }));
        //登陸失败
        loginHelper.setOnLoginFailedListener(message -> toast(getResources().getString(R.string.app_login_failed)+":"+message));
    }
}
