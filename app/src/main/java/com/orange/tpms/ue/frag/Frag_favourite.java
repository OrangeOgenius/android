package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.FavouriteAdapter;
import com.orange.tpms.bean.FavouriteFragBean;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.helper.FavouriteHelper;
import com.orange.tpms.widget.TitleWidget;
import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_favourite extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_favourite)
    RecyclerView rvFovourite;//Fovourite

    private FavouriteAdapter favouriteAdapter;//Adapter
    private LinearLayoutManager layoutManager;//列表表格布局
    private FavouriteHelper favouriteHelper;//Helper
    private MMYFragBean mmyFragBean = new MMYFragBean();//目标信息

    @Override
    public int onInflateLayout() {
        return R.layout.frag_favourite;
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
        //设置标题
        twTitle.setTvTitle(R.string.app_my_favorite);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(activity);
        }
        rvFovourite.setLayoutManager(layoutManager);
        favouriteAdapter = new FavouriteAdapter(activity);
        rvFovourite.setAdapter(favouriteAdapter);
        //点击事件
        favouriteAdapter.setOnItemClickListener((pos, mMyBean) -> {
            toFrag(mmyFragBean.getTargetClass(),true,false,mMyBean);
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        favouriteHelper = new FavouriteHelper();
        //读取结果回调
        favouriteHelper.setOnGetFavouriteListener((isEmpty,arrayList) -> {
            if(!isEmpty){
                favouriteAdapter.setItems(arrayList);
                favouriteAdapter.notifyDataSetChanged();
            }else{
                toast(R.string.app_myfavorite_is_empty);
            }
        });
        //读取最爱列表
        favouriteHelper.getFavourite(activity);
    }
}
