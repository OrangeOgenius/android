package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
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
            setStatus(carStatus,ivLeftTopWheel);
        }else if(carLocation == CAR_LOCATION.TOP_RIGHT){
            setStatus(carStatus,ivRightTopWheel);
        }else if(carLocation == CAR_LOCATION.BOTTOM_LEFT){
            setStatus(carStatus,ivLeftBottomWheel);
        }else if(carLocation == CAR_LOCATION.BOTTOM_RIGHT){
            setStatus(carStatus,ivRightBottomWheel);
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
        setStatus(topLeft,ivLeftTopWheel);
        setStatus(topRight,ivRightTopWheel);
        setStatus(bottomLeft,ivLeftBottomWheel);
        setStatus(bottomRight,ivRightBottomWheel);
    }

    /**
     * 单个设置轮胎状态
     * @param status 状态
     * @param imageView 轮胎容器
     */
    private void setStatus(CAR_STATUS status, ImageView imageView){
        if(status == CAR_STATUS.DEFAULT){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel);
        }else if(status == CAR_STATUS.NORMAL){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel_ok);
        }else if(status == CAR_STATUS.BAD){
            imageView.setBackgroundResource(R.mipmap.iv_car_wheel_failed);
        }
    }
}
