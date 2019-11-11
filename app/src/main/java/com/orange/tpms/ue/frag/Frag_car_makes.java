package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.CarLogoAdapter;
import com.orange.tpms.adapter.ShowItemImage;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.helper.MMYHelper;
import com.orange.tpms.mmySql.Item;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import java.util.ArrayList;

/**
 * 选车的牌子
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_car_makes extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_makes)
    RecyclerView rvMakes;//Makes
    @BindView(R.id.tv_content)
    TextView tvContent;//Title
    private ArrayList<Item> make=new ArrayList<>();
    private GridLayoutManager layoutManager;//列表表格布局
    private ShowItemImage carLogoAdapter;//Adapter适配器
    public MMYFragBean mmyFragBean = new MMYFragBean();//目标信息

    @Override
    public int onInflateLayout() {
        return R.layout.frag_car_makes;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onNexts(Object o) {
        if(o instanceof MMYFragBean){
            mmyFragBean = (MMYFragBean)o;
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置make
//        make=((MainActivity)activity).itemDAO.getMake(activity);
        //设置标题
        twTitle.setTvTitle(R.string.app_select_car_makes);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new GridLayoutManager(activity, 2);
        }
        rvMakes.setLayoutManager(layoutManager);
//        carLogoAdapter = new ShowItemImage(this,make);
        rvMakes.setAdapter(carLogoAdapter);
        //点击事件
//        carLogoAdapter.setOnItemClickListener((pos, content) -> {
//            mmyFragBean.setMmyItemBean(content);
//            toFrag(Frag_car_model.class,false,true,mmyFragBean);
//        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
//        //Helper
//        MMYHelper mmyHelper = new MMYHelper();
//        //读取结果回调
//        mmyHelper.setOnGetCarMakesListener((isEmpty, arrayList) -> {
//            if(!isEmpty){
//                carLogoAdapter.setItems(arrayList);
//                carLogoAdapter.notifyDataSetChanged();
//            }
//        });
//        //读取车的 Make Logo
//        mmyHelper.GetCarMakes(activity);
    }
}
