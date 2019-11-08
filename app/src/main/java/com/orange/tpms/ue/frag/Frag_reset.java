package com.orange.tpms.ue.frag;

import android.os.Handler;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 重设密码
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_reset extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading

    @Override
    public int onInflateLayout() {
        return R.layout.frag_reset;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_submit)
    private void send(View view){
        lwLoading.show(R.mipmap.img_data_upload_and_loading,getResources().getString(R.string.app_data_uploading),true);
        new Handler().postDelayed(() -> {
            lwLoading.show(R.mipmap.img_email,getResources().getString(R.string.app_email_send),false);
        },1500);
    }

    @Override
    public boolean onBackPresss(){
        if(lwLoading.isShown()){
            lwLoading.hide();
        }else{
            back();
        }
        return true;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_reset);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }
}
