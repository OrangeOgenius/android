package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 通用的对话框，单选、双选、渐变显示与渐变隐藏
 * Created by haide.yin() on 2019/2/25 10:38.
 */
public class NormalDialogWidget extends PercentRelativeLayout {

    @BindView(R.id.tv_content)
    private TextView tvContent;//提示内容
    @BindView(R.id.tv_click_ok)
    private TextView tvOk;//确定
    @BindView(R.id.tv_click_cancel)
    private TextView tvCancel;//取消
    @BindView(R.id.v_bottom_split)
    private View tvSplit;//分割线

    public NormalDialogWidget(Context context) {
        this(context, null, 0);
    }

    public NormalDialogWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalDialogWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView( View.inflate(context, R.layout.widget_normal_dialog, this));
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

    /**
     * 单选：只有tvOk可以用
     */
    public void setSingleChoice(){
        tvSplit.setVisibility(GONE);
        tvCancel.setVisibility(GONE);
        tvOk.setVisibility(VISIBLE);
    }

    /**
     * 双选
     */
    public void setDoubleChoice(){
        tvSplit.setVisibility(VISIBLE);
        tvCancel.setVisibility(VISIBLE);
        tvOk.setVisibility(VISIBLE);
    }

    /**
     * 内容
     *
     * @return the text view
     */
    public TextView getTvContent() {
        return this.tvContent;
    }

    /**
     * 确定
     *
     * @return the text view
     */
    public TextView getTvOk() {
        return this.tvOk;
    }

    /**
     * 取消
     *
     * @return the text view
     */
    public TextView getTvCancel() {
        return this.tvCancel;
    }

}
