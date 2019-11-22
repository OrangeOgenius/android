package com.orange.tpms.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.utils.OggUtils;
import com.orange.tpms.utils.ViewVisibleUtils;

/**
 * 通用的编辑框
 * Created by haide.yin() on 2019/3/11 10:38.
 */
public class EditDialogWidget extends PercentRelativeLayout {

    @BindView(R.id.tv_title)
    private TextView tvTitle;//标题
    @BindView(R.id.tv_click_ok)
    private TextView tvOk;//确定
    @BindView(R.id.tv_click_cancel)
    private TextView tvCancel;//取消
    @BindView(R.id.et_content)
    private ClearEditText etContent;//内容页
    @BindView(R.id.v_bottom_split)
    private View tvSplit;//分隔线

    private Object object;//缓存的对象

    public EditDialogWidget(Context context) {
        this(context, null, 0);
    }

    public EditDialogWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditDialogWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Rocket.bindView(View.inflate(context, R.layout.widget_edit_dialog, this));
        initView();
    }

    private void initView(){
        tvCancel.setOnClickListener(v -> hide());
        tvOk.setOnClickListener(v -> {
            if (mDoneListener != null){
                mDoneListener.onDone(OggUtils.getEd(etContent));
            }
        });
    }

    /**
     * 缓存对象
     */
    public Object getObject() {
        return object;
    }

    /**
     * 读取对象
     */
    public void setObject(Object object) {
        this.object = object;
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

    /* ************************************* 点击done之后的接口回调 ****************************************** */

    private DoneListener mDoneListener;

    public void setDoneListener(DoneListener doneListener) {
        this.mDoneListener = doneListener;
    }

    public interface DoneListener {
        void onDone(String content);
    }

}
