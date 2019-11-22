package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentLinearLayout;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 扫码提示的页面
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class ScanWidget extends PercentRelativeLayout {

    @BindView(R.id.pll_forground)
    private PercentLinearLayout pllForground;//前景

    public ScanWidget(Context context) {
        this(context, null, 0);
    }

    public ScanWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_scan, this));
        initView();
    }

    /**
     * 初始化
     */
    private void initView(){
        pllForground.setOnClickListener(v -> hide());
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
}
