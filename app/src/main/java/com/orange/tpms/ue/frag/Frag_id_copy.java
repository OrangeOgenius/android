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
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.bean.ScanQrCodeBean;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import java.util.ArrayList;
import java.util.List;

import bean.mmy.MMyBean;

/**
 * ID COPY
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_id_copy extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.b1)
    RelativeLayout b1;
    @BindView(R.id.b2)
    RelativeLayout b2;
    @BindView(R.id.b3)
    RelativeLayout b3;
    @Override
    public int onInflateLayout() {
        return R.layout.frag_id_copy;
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
//                toFrag(Frag_id_copy_original.class,false,true,mMyBean);
//            }
//        } else if(o instanceof MMyBean){
//            MMyBean mMyBean = (MMyBean)o;
//            toFrag(Frag_id_copy_original.class,false,true,mMyBean);
//        }
        PublicBean.position= PublicBean.複製傳感器;
    }

    /**
     * 功能列表
     */
    private List<FunctionSelectBean> getFunctionList(){
        //列表信息
        List<FunctionSelectBean> funcList = new ArrayList<>();
        //扫码的列
        FunctionSelectBean scanFunc = new FunctionSelectBean(R.drawable.bt_scan_code_selecter,true,getString(R.string.app_scan_code),Frag_scan_info.class);
        scanFunc.setTargetObject(new ScanQrCodeBean(ScanQrCodeBean.TYPE_MMY));
        funcList.add(scanFunc);
        //选择MMY的列
        FunctionSelectBean mmyFunc = new FunctionSelectBean(R.drawable.bt_mmy_selecter,true,getString(R.string.app_mmy),Frag_car_makes.class);
        mmyFunc.setTargetObject(new MMYFragBean(getClass()));
        funcList.add(mmyFunc);
        //历史记录的MMY列
        FunctionSelectBean favoriteFunc = new FunctionSelectBean(R.drawable.bt_my_favourite_selecter,true,getString(R.string.app_my_favorite),Frag_favourite.class);
        favoriteFunc.setTargetObject(new MMYFragBean(getClass()));
        funcList.add(favoriteFunc);
        //其他列
        funcList.add(new FunctionSelectBean(R.drawable.bt_low_frequency_selecter,false,getString(R.string.app_low_frequency),Frag_favourite.class,true));
        funcList.add(new FunctionSelectBean(R.drawable.bt_low_frequency_selecter,true,getString(R.string.app_low_frequency),Frag_low_frequency.class));
        return funcList;
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_home_id_copy);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //初始化列表
        b1.setOnClickListener(v -> {
            PublicBean.ScanType=ScanQrCodeBean.TYPE_MMY;
            toFrag(Frag_scan_info.class, false, true, "");
        });
        b2.setOnClickListener(v -> {
            toFrag(Frag_car_makes.class, false, true, "");
        });
        b3.setOnClickListener(v -> {
            toFrag(Frag_favourite.class, false, true, "");
        });
    }
}
