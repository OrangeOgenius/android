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
 * 通用的汽车结构
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class CarWidget extends PercentRelativeLayout {

    @BindView(R.id.iv_wheel_top_left)
    public ImageView ivLeftTopWheel;//左上轮胎
    @BindView(R.id.iv_wheel_top_right)
    public ImageView ivRightTopWheel;//右上轮胎
    @BindView(R.id.iv_wheel_bottom_left)
    public ImageView ivLeftBottomWheel;//左下轮胎
    @BindView(R.id.iv_wheel_bottom_right)
    public ImageView ivRightBottomWheel;//右下轮胎
    @BindView(R.id.lft)
    public TextView topLeftT;
    @BindView(R.id.rft)
    public TextView topRightT;
    @BindView(R.id.rbt)
    public TextView buttonRighT;
    @BindView(R.id.lbt)
    public TextView buttonLeftT;

    /**
     * 方位枚举
     */
    public enum CAR_LOCATION{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT

    }
    /**
     * 状态枚举
     */
    public enum CAR_STATUS{
        DEFAULT,//默认显示
        NORMAL,//检测正常
        BAD,//检测损坏
    }

    public CarWidget(Context context) {
        this(context, null, 0);
    }

    public CarWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_car, this));
    }

    /**
     * 设置轮胎状态
     * @param carLocation 位置
     * @param carStatus 状态
     */
    public void setCarStatus(CAR_LOCATION carLocation,CAR_STATUS carStatus){
        if(carLocation == CAR_LOCATION.TOP_LEFT){
            setStatus(carStatus,ivLeftTopWheel,topLeftT);
        }else if(carLocation == CAR_LOCATION.TOP_RIGHT){
            setStatus(carStatus,ivRightTopWheel,topRightT);
        }else if(carLocation == CAR_LOCATION.BOTTOM_LEFT){
            setStatus(carStatus,ivLeftBottomWheel,buttonLeftT);
        }else if(carLocation == CAR_LOCATION.BOTTOM_RIGHT){
            setStatus(carStatus,ivRightBottomWheel,buttonRighT);
        }
    }

    /**
     * 设置四个轮胎状态
     * @param topLeft 左上轮胎
     * @param topRight 右上轮胎
     * @param bottomLeft 左下轮胎
     * @param bottomRight 右下轮胎
     */
    public void setCarStatus(CAR_STATUS topLeft,CAR_STATUS topRight,CAR_STATUS bottomLeft,CAR_STATUS bottomRight){
        setStatus(topLeft,ivLeftTopWheel,topLeftT);
        setStatus(topRight,ivRightTopWheel,topRightT);
        setStatus(bottomLeft,ivLeftBottomWheel,buttonRighT);
        setStatus(bottomRight,ivRightBottomWheel,buttonLeftT);
    }

    /**
     * 单个设置轮胎状态
     * @param status 状态
     * @param imageView 轮胎容器
     */
    private void setStatus(CAR_STATUS status, ImageView imageView, TextView textView){
        if(status == CAR_STATUS.DEFAULT){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel);
            textView.setBackgroundResource(R.mipmap.img_wheel_circular_pattern);
        }else if(status == CAR_STATUS.NORMAL){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel_ok);
            textView.setBackgroundResource(R.mipmap.icon__tire_ok);
        }else if(status == CAR_STATUS.BAD){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel_failed);
            textView.setBackgroundResource(R.mipmap.icon_tire_fail);
        }
    }
}
