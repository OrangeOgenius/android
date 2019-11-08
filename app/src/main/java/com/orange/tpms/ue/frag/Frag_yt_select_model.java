package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.adapter.YTSelectModelAdapter;
import com.orange.tpms.bean.YTSelectModelBean;
import com.orange.tpms.widget.TitleWidget;

import java.util.ArrayList;
import java.util.List;


/**
 * 启动页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_yt_select_model extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_select_model)
    RecyclerView rvSelectModel;//SelectModel

    private LinearLayoutManager layoutManager;//列表表格布局
    private YTSelectModelAdapter selectModelAdapter;//adapter
    private List<YTSelectModelBean> selectModelBeans = new ArrayList<>();

    @Override
    public int onInflateLayout() {
        return R.layout.frag_yt_select_model;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {
        if(o instanceof String){
            String type = (String)o;
            //设置标题
            twTitle.setTvTitle(type);
            selectModelBeans.clear();
            if(type.equals(YTSelectModelBean.TYPR_NORMAL)){//普通车
                selectModelBeans.add(new YTSelectModelBean("二轮配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("四轮配置",true,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("四轮+1配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("六轮配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("六轮+1配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("八轮配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("八轮+1配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("十轮配置",false,type,YTSelectModelBean.HEX_NORMAL));
                selectModelBeans.add(new YTSelectModelBean("十轮+1配置",false,type,YTSelectModelBean.HEX_NORMAL));
            }else if(type.equals(YTSelectModelBean.TYPR_HIGH)){//高级车
                selectModelBeans.add(new YTSelectModelBean("二轮配置",false,type,YTSelectModelBean.HEX_HIGHT));
                selectModelBeans.add(new YTSelectModelBean("六轮配置",true,type,YTSelectModelBean.HEX_HIGHT));
            }
            selectModelAdapter.setItems(selectModelBeans);
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题栏
        twTitle.setBgColor(R.color.color_yt);
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(activity);
        }
        rvSelectModel.setLayoutManager(layoutManager);
        selectModelAdapter = new YTSelectModelAdapter(activity);
        rvSelectModel.setAdapter(selectModelAdapter);
        //点击事件
        selectModelAdapter.setOnItemClickListener((pos, selectModelBean) -> {
            toFrag(Frag_yt_read_sensor.class,false,true,selectModelBean);
        });
    }
}
