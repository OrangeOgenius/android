package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.RelativeLayout;
import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.FunctionSelectAdapter;
import com.orange.tpms.bean.FunctionSelectBean;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.bean.ScanQrCodeBean;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.TitleWidget;

import bean.mmy.MMyBean;
import com.de.rocket.ue.injector.BindView;

import java.util.ArrayList;
import java.util.List;

/**
 * Check Sensor
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_check_sensor extends Frag_base {
    @BindView(R.id.b1)
    RelativeLayout b1;//Title
    @BindView(R.id.b2)
    RelativeLayout b2;//Title
    @BindView(R.id.b3)
    RelativeLayout b3;//Title
    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    @Override
    public int onInflateLayout() {
        return R.layout.frag_check_sensor;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {
//        if (o instanceof ScanQrCodeBean) {
//            ScanQrCodeBean scanQrCodeBean = (ScanQrCodeBean) o;
//            if(scanQrCodeBean.getScanType() == ScanQrCodeBean.TYPE_MMY){
//                MMyBean mMyBean = new MMyBean();
//                mMyBean.setHex(scanQrCodeBean.getMmyQrCodeBean().getMmyCode());
//                toFrag(Frag_check_sensor_information.class,false,true,mMyBean);
//            }
//        }else if(o instanceof MMyBean){
//            MMyBean mMyBean = (MMyBean)o;
//            toFrag(Frag_check_sensor_information.class,false,true,mMyBean);
//        }
        MainActivity.position=MainActivity.檢查傳感器;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_home_check_sensor);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //初始化列表
        b1.setOnClickListener(v -> {
            MainActivity.ScanType=ScanQrCodeBean.TYPE_MMY;
            toFrag(Frag_scan_info.class, false, true, "");
        });
        b2.setOnClickListener(v -> {
            toFrag(Frag_car_makes.class, false, true, "");
        });
        b3.setOnClickListener(v -> {
            toFrag(Frag_favourite.class, false, true, "");
        });
        //配置RecyclerView
//        rvFunction.setLayoutManager(new GridLayoutManager(activity, 3));
//        FunctionSelectAdapter functionSelectAdapter = new FunctionSelectAdapter(activity);
//        functionSelectAdapter.setItems(getFunctionList());
//        rvFunction.setAdapter(functionSelectAdapter);
//        //点击事件
//        functionSelectAdapter.setOnItemClickListener((index, functionSelectBean) -> {
//            toFrag(functionSelectBean.getTargetClass(),false,true,functionSelectBean.getTargetObject());
//        });
    }
}
