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
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 通用的Loading框，用来替代常用控件Dialog
 * Created by haide.yin() on 2019/4/2 10:38.
 */
public class LoadingWidget extends PercentRelativeLayout {

    @BindView(R.id.v_bg)
    private View viewBg;//背景
    @BindView(R.id.iv_loading)
    private ImageView ivLoading;//图片
    @BindView(R.id.tv_loading)
    private TextView tvLoading;//Loading文字
    @BindView(R.id.lpv_loading)
    private LoadingPointWidget loadingPointWidget;//point

    public LoadingWidget(Context context) {
        this(context, null, 0);
    }

    public LoadingWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_loading, this));
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView(){
        viewBg.setOnClickListener(v -> hide());
    }

    /**
     * 显示动画
     */
    public void show() {
        loadingPointWidget.show();
        ViewVisibleUtils.showFade(this, 0f, 1f, 400);
    }

    /**
     * 显示动画
     */
    public void show(String content) {
        loadingPointWidget.show();
        if(content != null){
            tvLoading.setVisibility(VISIBLE);
            tvLoading.setText(content);
        }else{
            tvLoading.setVisibility(GONE);
        }
        ViewVisibleUtils.showFade(this, 0f, 1f, 400);
    }

    /**
     * 显示动画
     */
    public void show(int imgid,String content) {
        loadingPointWidget.show();
        if(imgid == -1){
            ivLoading.setVisibility(GONE);
        }else{
            ivLoading.setVisibility(VISIBLE);
            ivLoading.setImageResource(imgid);
        }
        if(content != null){
            tvLoading.setVisibility(VISIBLE);
            tvLoading.setText(content);
        }else{
            tvLoading.setVisibility(GONE);
        }
        ViewVisibleUtils.showFade(this, 0f, 1f, 400);
    }

    /**
     * 显示动画
     */
    public void show(int imgid,String content,boolean showPoint) {
        if(content != null){
            tvLoading.setVisibility(VISIBLE);
            tvLoading.setText(content);
        }else{
            tvLoading.setVisibility(GONE);
        }
        if(imgid == -1){
            ivLoading.setVisibility(GONE);
        }else{
            ivLoading.setVisibility(VISIBLE);
            ivLoading.setImageResource(imgid);
        }
        if(showPoint){
            loadingPointWidget.setVisibility(VISIBLE);
            loadingPointWidget.show();
        }else{
            loadingPointWidget.setVisibility(GONE);
        }
        ViewVisibleUtils.showFade(this, 0f, 1f, 400);
    }

    /**
     * 隐藏
     */
    public void hide() {
        loadingPointWidget.hide();
        ViewVisibleUtils.hideFade(this, 1f, 0f, 400);
    }

    /**
     * 获取转圈的文本
     * @return tvLoading
     */
    public TextView getTvLoading(){
        return this.tvLoading;
    }

    /**
     * 更改加载text
     * @param text 加载的text
     */
    public void setLoadingText(String text){
        tvLoading.setText(text);
    }

    /**
     * 更改加载text
     * @param textId 加载的text的id
     */
    public void setLoadingText(int textId){
        tvLoading.setText(textId);
    }

    /**
     * 设置text显示或隐藏
     * @param visibility 显示或隐藏
     */
    public void setLoadingTextVisible(int visibility){
        tvLoading.setVisibility(visibility);
    }

}
