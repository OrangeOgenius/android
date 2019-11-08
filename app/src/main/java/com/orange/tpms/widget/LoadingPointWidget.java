package com.orange.tpms.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.ViewVisibleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的Loading框，用来替代常用控件Dialog
 * Created by haide.yin() on 2019/4/2 10:38.
 */
public class LoadingPointWidget extends PercentRelativeLayout {

    @BindView(R.id.tv_point_1)
    private TextView tvPoint1;//point
    @BindView(R.id.tv_point_2)
    private TextView tvPoint2;//point
    @BindView(R.id.tv_point_3)
    private TextView tvPoint3;//point
    @BindView(R.id.tv_point_4)
    private TextView tvPoint4;//point
    @BindView(R.id.tv_point_5)
    private TextView tvPoint5;//point

    private int index = 0;
    private int delayTimeMil = 200;
    private List<TextView> pointViews = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable = this::showPoint;

    public LoadingPointWidget(Context context) {
        this(context, null, 0);
    }

    public LoadingPointWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPointWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_loading_point, this));
        initView();
    }

    /**
     * 初始化
     */
    public void initView(){
        pointViews.add(tvPoint1);
        pointViews.add(tvPoint2);
        pointViews.add(tvPoint3);
        pointViews.add(tvPoint4);
        pointViews.add(tvPoint5);
    }

    /**
     * 显示点
     */
    private void showPoint(){
        if(pointViews.size() > index){
            for(int i = 0;i<pointViews.size();i++){
                if(i <= index){
                    pointViews.get(i).setVisibility(VISIBLE);
                }else{
                    pointViews.get(i).setVisibility(INVISIBLE);
                }
            }
        }
        index ++;
        if(index >= pointViews.size()){
            index = 0;
        }
        handler.postDelayed(runnable,delayTimeMil);
    }

    /**
     * 显示动画
     */
    public void show() {
        handler.postDelayed(runnable,delayTimeMil);
        ViewVisibleUtils.showFade(this, 0f, 1f, 400);
    }

    /**
     * 隐藏
     */
    public void hide() {
        handler.removeCallbacks(runnable);
        ViewVisibleUtils.hideFade(this, 1f, 0f, 400);
    }
}
