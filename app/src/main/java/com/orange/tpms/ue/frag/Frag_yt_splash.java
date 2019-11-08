package com.orange.tpms.ue.frag;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;

import com.de.rocket.ue.injector.BindView;

/**
 * 启动页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_yt_splash extends Frag_base {

    @BindView(R.id.tv_version)
    TextView tvVersion;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_yt_splash;
    }

    @Override
    public void initViewFinish(View inflateView) {
        //设置版本号
        tvVersion.setText(String.valueOf("V"+getVersionName(activity)));
        //如果此处作为第一个页面并且有跳转行为, 必须使用handler进行包裹处理
        //可以设置0毫秒，以下设置为1.5秒模拟配置页信息处理
        new Handler().postDelayed(this::initFinish, 0);
    }

    @Override
    public void onNexts(Object o) {

    }

    /**
     * 初始化
     */
    private void initFinish() {
        toFrag(Frag_yt_car_type.class,true,true,null);
    }

    /**
     * 获取版本名称
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
