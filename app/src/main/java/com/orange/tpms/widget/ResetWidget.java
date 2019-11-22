package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 回复出厂设置
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class ResetWidget extends PercentRelativeLayout {

    @BindView(R.id.bt_reset)
    private Button btReset;
    @BindView(R.id.bt_cancel)
    private Button btCancel;

    public ResetWidget(Context context) {
        this(context, null, 0);
    }

    public ResetWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResetWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_reset, this));
        initView();
    }

    /**
     * 初始化
     */
    private void initView(){
        btCancel.setOnClickListener(v -> hide());
    }

    /**
     * reset监听
     * @param onClickListener 回调
     */
    public void setOnResetClickListener(OnClickListener onClickListener){
        btReset.setOnClickListener(onClickListener);
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
