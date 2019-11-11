package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.YearAdapter;
import com.orange.tpms.bean.FavouriteFragBean;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.bean.MMYItemBean;
import com.orange.tpms.helper.MMYHelper;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.TitleWidget;

import bean.mmy.MMyBean;
import com.de.rocket.ue.injector.BindView;
import com.orango.electronic.orangetxusb.Adapter.ShowYear;

import java.util.ArrayList;

/**
 * 选择年份
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_car_year extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_year)
    RecyclerView rvYear;//Year
    @BindView(R.id.tv_content)
    TextView tvContent;//Title

    private MMYHelper mmyHelper;//Helper
    private LinearLayoutManager layoutManager;//列表表格布局
    private ShowYear yearAdapter;//Adapter
    private ArrayList<String> year=new ArrayList<>();

    @Override
    public int onInflateLayout() {
        return R.layout.frag_car_year;
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
//        year=((MainActivity)activity).itemDAO.getYear(PublicBean.SelectMake,PublicBean.SelectModel);
        //设置标题
        twTitle.setTvTitle(R.string.app_select_year);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(activity);
        }
        rvYear.setLayoutManager(layoutManager);
//        yearAdapter = new ShowYear(year,this);
        rvYear.setAdapter(yearAdapter);
        //点击事件
//        yearAdapter.setOnItemClickListener((pos, mMyBean) -> {
//            toFrag(mmyFragBean.getTargetClass(),false,false,mMyBean,true);
//        });
    }


}
