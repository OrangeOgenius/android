package com.orange.tpms.ue.frag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.ModuleAdapter;
import com.orange.tpms.bean.FavouriteFragBean;
import com.orange.tpms.bean.MMYFragBean;
import com.orange.tpms.bean.MMYItemBean;
import com.orange.tpms.helper.MMYHelper;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;
import com.orango.electronic.orangetxusb.Adapter.ShowModel;

import java.util.ArrayList;

/**
 * 选择型号
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_car_model extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_model)
    RecyclerView rvModel;//Model
    @BindView(R.id.tv_content)
    TextView tvContent;//Title

    private MMYHelper mmyHelper;//Helper
    private LinearLayoutManager layoutManager;//列表表格布局
    private ShowModel moduleAdapter;//Adapter
    private ArrayList<String> modle=new ArrayList<>();
    @Override
    public int onInflateLayout() {
        return R.layout.frag_car_model;
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
        modle=((MainActivity)activity).itemDAO.getModel(MainActivity.SelectMake);
        //设置标题
        twTitle.setTvTitle(R.string.app_select_car_model);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(activity);
        }
        rvModel.setLayoutManager(layoutManager);
        moduleAdapter = new ShowModel(modle,this);
        rvModel.setAdapter(moduleAdapter);
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     * @param filename 文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                filename =  filename.substring(0, dot);
                int under = filename.lastIndexOf('_');
                if ((under >-1) && (under < (filename.length()))) {
                    filename =  filename.substring(under+1, filename.length());
                }
            }
        }
        return filename;
    }
}
