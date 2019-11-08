package com.orange.tpms.ue.frag;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.FavouriteSettingAdapter;
import com.orange.tpms.bean.FavouriteFragBean;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.helper.FavouriteHelper;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

import bean.mmy.MMyBean;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_favourite extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_favourite)
    RecyclerView rvFovourite;//Fovourite

    private FavouriteSettingAdapter favouriteSettingAdapter;//Adapter
    private LinearLayoutManager layoutManager;//列表表格布局
    private FavouriteHelper favouriteHelper;//Helper

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_favourite;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onNexts(Object o) {
        if(o instanceof MMyBean){
            //读取最爱列表
            favouriteHelper.getFavouriteSetting(activity);
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
        favouriteSettingAdapter = new FavouriteSettingAdapter(activity);
        rvFovourite.setAdapter(favouriteSettingAdapter);
        //删除事件
        favouriteSettingAdapter.setOnItemDeleteListener((pos, mMyBean) -> {
            favouriteHelper.deleteFavourite(activity,mMyBean);
        });
        //新增事件
        favouriteSettingAdapter.setOnItemAddListener((pos, mMyBean) -> {
            MMYFragBean mmyFragBean = new MMYFragBean();
            mmyFragBean.setTargetClass(Frag_setting_favourite.class);
            toFrag(Frag_car_makes.class,false,true,mmyFragBean);
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
                favouriteSettingAdapter.setItems(arrayList);
                favouriteSettingAdapter.notifyDataSetChanged();
            }else{
                toast(R.string.app_myfavorite_is_empty);
            }
        });
        //读取最爱列表
        favouriteHelper.getFavouriteSetting(activity);
    }
}
