package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;

/**
 * 通用的标题栏
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class TitleWidget extends PercentRelativeLayout {

    @BindView(R.id.tv_title)
    private TextView tvTitle;//标题
    @BindView(R.id.iv_back)
    private ImageView ivBack;//返回
    @BindView(R.id.prl_contaner)
    private PercentRelativeLayout prlContaner;//容器
    @BindView(R.id.prl_titlebar)
    private PercentRelativeLayout prlTitleBar;//标题栏

    public TitleWidget(Context context) {
        this(context, null, 0);
    }

    public TitleWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_title_bar, this));
        initView(context);
    }

    /**
     * 页面初始化
     */
    private void initView(Context context){
        //向下偏移一个状态栏的高度
        setStatusBarOffset(context,prlTitleBar);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 使指定的View向下偏移一个状态栏高度，留出状态栏空间，主要用于设置沉浸式后，Fragment页面特定需求
     *
     * @param context        需要设置的context
     * @param needOffsetView 需要偏移的View
     */
    private void setStatusBarOffset(Context context, View needOffsetView) {
        if (needOffsetView != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(context),
                    layoutParams.rightMargin, layoutParams.bottomMargin);
        }
    }

    /**
     * 设置背景色
     * @param id resourceid
     */
    public void setBgColor(int id){
        prlContaner.setBackgroundResource(id);
    }

    /**
     * 设置标题
     * @param id resourceid
     */
    public void setTvTitle(int id){
        tvTitle.setText(id);
    }

    /**
     * 设置标题
     * @param title title
     */
    public void setTvTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 返回回调
     * @param clickListener 回调
     */
    public void setOnBackListener(OnClickListener clickListener){
        ivBack.setOnClickListener(clickListener);
    }

    /**
     * 设置显示或者隐藏
     * @param visible 显示或者隐藏
     */
    public void setTvBackVisible(int visible){
        ivBack.setVisibility(visible);
    }
}
