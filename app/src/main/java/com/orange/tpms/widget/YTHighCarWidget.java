package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;

/**
 * 高级车汽车结构
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class YTHighCarWidget extends PercentRelativeLayout {

    @BindView(R.id.iv_wheel_first)
    private ImageView ivWheelFirst;//1
    @BindView(R.id.iv_wheel_second)
    private ImageView ivWheelSecond;//2
    @BindView(R.id.iv_wheel_third)
    private ImageView ivWheelThird;//3
    @BindView(R.id.iv_wheel_forth)
    private ImageView ivWheelForth;//4
    @BindView(R.id.iv_wheel_fifth)
    private ImageView ivWheelFifth;//5
    @BindView(R.id.iv_wheel_sixth)
    private ImageView ivWheelSixth;//6
    @BindView(R.id.tv_wheel_first)
    private TextView tvWheelFirst;//1
    @BindView(R.id.tv_wheel_second)
    private TextView tvWheelSecond;//2
    @BindView(R.id.tv_wheel_third)
    private TextView tvWheelThird;//3
    @BindView(R.id.tv_wheel_forth)
    private TextView tvWheelForth;//4
    @BindView(R.id.tv_wheel_fifth)
    private TextView tvWheelFifth;//5
    @BindView(R.id.tv_wheel_sixth)
    private TextView tvWheelSixth;//6

    /**
     * 车轮位置
     */
    public enum LOCATION{
        FIRST("左上轮"),
        SECONG("右上轮"),
        THIRD("左后外轮"),
        FORTH("左后内轮"),
        FIFTH("右后外轮"),
        SIXTH("右后内轮"),;

        private String car;

        LOCATION(String car){
            this.car = car;
        }

        public String toString(){
            return this.car;
        }
    }

    /**
     * 状态枚举
     */
    public enum STATUS{
        DEFAULT,//默认显示,显示黑色
        NORMAL,//检测正常，显示绿色
        BAD,//检测损坏，显示红色
    }

    public YTHighCarWidget(Context context) {
        this(context, null, 0);
    }

    public YTHighCarWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YTHighCarWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_yt_high_car, this));
        initView();
    }

    /**
     * 初始化
     */
    private void initView(){
        tvWheelFirst.setOnClickListener(v -> wheelClickNext(LOCATION.FIRST,1));
        tvWheelSecond.setOnClickListener(v -> wheelClickNext(LOCATION.SECONG,2));
        tvWheelThird.setOnClickListener(v -> wheelClickNext(LOCATION.THIRD,3));
        tvWheelForth.setOnClickListener(v -> wheelClickNext(LOCATION.FORTH,4));
        tvWheelFifth.setOnClickListener(v -> wheelClickNext(LOCATION.FIFTH,5));
        tvWheelSixth.setOnClickListener(v -> wheelClickNext(LOCATION.SIXTH,6));
    }

    /**
     * 设置四个轮胎状态
     * @param index 对应的车轮
     */
    public void setCarStatus(STATUS status,int index){
        if(index == 1){
            setStatus(status,ivWheelFirst,true);
        }else if(index == 2){
            setStatus(status,ivWheelSecond,false);
        }else if(index == 3){
            setStatus(status,ivWheelThird,true);
        }else if(index == 4){
            setStatus(status,ivWheelForth,false);
        }else if(index == 5){
            setStatus(status,ivWheelFifth,false);
        }else if(index == 6){
            setStatus(status,ivWheelSixth,false);
        }
    }

    /**
     * 单个设置轮胎状态
     * @param status 状态
     * @param imageView 轮胎容器
     */
    private void setStatus(STATUS status, ImageView imageView,boolean isOut){
        if(status == STATUS.DEFAULT){
            if(isOut){
                imageView.setBackgroundResource(R.mipmap.yt_tire_black_out);
            }else{
                imageView.setBackgroundResource(R.mipmap.yt_tire_black);
            }
        }else if(status == STATUS.NORMAL){
            if(isOut){
                imageView.setBackgroundResource(R.mipmap.yt_tire_green_out);
            }else{
                imageView.setBackgroundResource(R.mipmap.yt_tire_green);
            }
        }else if(status == STATUS.BAD){
            if(isOut){
                imageView.setBackgroundResource(R.mipmap.yt_tire_red_out);
            }else{
                imageView.setBackgroundResource(R.mipmap.yt_tire_red);
            }
        }
    }

    /* *********************************  轮子的点击事件  ************************************** */

    private OnWheelClickListener onWheelClickListener;

    public void wheelClickNext(LOCATION location,int index) {
        if (onWheelClickListener != null) {
            onWheelClickListener.onWheelClick(location,index);
        }
    }

    public void setOnWheelClickListener(OnWheelClickListener onWheelClickListener) {
        this.onWheelClickListener = onWheelClickListener;
    }

    public interface OnWheelClickListener {
        void onWheelClick(LOCATION location,int index);
    }
}
