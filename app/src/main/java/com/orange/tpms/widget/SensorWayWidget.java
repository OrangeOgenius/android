package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentLinearLayout;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 读取Sensor的方式选择页面
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class SensorWayWidget extends PercentRelativeLayout {

    public static int SELECT_TYPE_SCAN = 0;//SCAN
    public static int SELECT_TYPE_TRIGGER = 1;//TRIGGER
    public static int SELECT_TYPE_KEYIN = 2;//kEY IN

    @BindView(R.id.tv_content)
    private TextView tvTitle;//title
    @BindView(R.id.v_bg)
    private View vBG;//bg
    @BindView(R.id.pll_scan)
    public PercentLinearLayout pllScan;
    @BindView(R.id.pll_trigger)
    public PercentLinearLayout pllTrigger;
    @BindView(R.id.pll_keyin)
    public PercentLinearLayout pllKeyin;

    private int selectType = SELECT_TYPE_SCAN;//当前选中的类型

    public SensorWayWidget(Context context) {
        this(context, null, 0);
    }

    public SensorWayWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorWayWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_sensor_way, this));
        initListener();
    }

    /**
     * 获取当前选中的类型
     */
    public int getSelectType() {
        return selectType;
    }

    /**
     * 初始化点击事件
     */
    private void initListener(){
        vBG.setOnClickListener(v -> {});
        pllScan.setOnClickListener(v -> {
            hide();
            selectType = SELECT_TYPE_SCAN;
            onScanClickNext();
        });
        pllTrigger.setOnClickListener(v -> {
            hide();
            selectType = SELECT_TYPE_TRIGGER;
            onTriggerClickNext();
        });
        pllKeyin.setOnClickListener(v -> {
            hide();
            selectType = SELECT_TYPE_KEYIN;
            onKeyinClickNext();
        });
    }

    /**
     * 设置标题
     */
    public void setTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 显示
     */
    public void show() {
        ViewVisibleUtils.showFade(this,0f,1f,400);
    }

    /**
     * 隐藏
     */
    public void hide() {
        ViewVisibleUtils.hideFade(this,1f,0f,400);
    }

    /* ***************************** ScanClick ***************************** */

    private OnScanClickListener onScanClickListener;

    // 接口类 -> OnScanClickListener
    public interface OnScanClickListener {
        void onScanClick();
    }

    // 对外暴露接口 -> setOnScanClickListener
    public void setOnScanClickListener(OnScanClickListener onScanClickListener) {
        this.onScanClickListener = onScanClickListener;
    }

    // 内部使用方法 -> ScanClickNext
    private void onScanClickNext() {
        if (onScanClickListener != null) {
            onScanClickListener.onScanClick();
        }
    }

    /* ***************************** TriggerClick ***************************** */

    private OnTriggerClickListener onTriggerClickListener;

    // 接口类 -> OnTriggerClickListener
    public interface OnTriggerClickListener {
        void onTriggerClick();
    }

    // 对外暴露接口 -> setOnTriggerClickListener
    public void setOnTriggerClickListener(OnTriggerClickListener onTriggerClickListener) {
        this.onTriggerClickListener = onTriggerClickListener;
    }

    // 内部使用方法 -> TriggerClickNext
    private void onTriggerClickNext() {
        if (onTriggerClickListener != null) {
            onTriggerClickListener.onTriggerClick();
        }
    }

    /* ***************************** KeyinClick ***************************** */

    private OnKeyinClickListener onKeyinClickListener;

    // 接口类 -> OnKeyinClickListener
    public interface OnKeyinClickListener {
        void onKeyinClick();
    }

    // 对外暴露接口 -> setOnKeyinClickListener
    public void setOnKeyinClickListener(OnKeyinClickListener onKeyinClickListener) {
        this.onKeyinClickListener = onKeyinClickListener;
    }

    // 内部使用方法 -> KeyinClickNext
    private void onKeyinClickNext() {
        if (onKeyinClickListener != null) {
            onKeyinClickListener.onKeyinClick();
        }
    }
}
