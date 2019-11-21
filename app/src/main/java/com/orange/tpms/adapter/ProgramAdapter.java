package com.orange.tpms.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.bean.ProgramItemBean;
import com.orange.tpms.utils.KeyboardUtil;
import com.orange.tpms.utils.ProgramFilter;
import com.orange.tpms.widget.ClearEditText;

public class ProgramAdapter extends BaseRecyclerAdapter<ProgramItemBean, ProgramAdapter.ViewHolder> {

    private int select;
    private OnItemClickListener onItemClickListener;
    int count;
    Activity activity;
    public ProgramAdapter(Context context, int count, Activity activity) {
         super(context);
         select = -1;
         this.count=count;
         this.activity=activity;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_program_detail, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        if(index == getItemCount()-1){
            holder.vBottomSep.setVisibility(View.VISIBLE);
        }
        //不显示软键盘
        KeyboardUtil.hideEditTextKeyboard(holder.etTitle);
        //全部大写
        holder.etTitle.setFilters(new InputFilter[] {new InputFilter.AllCaps(),new InputFilter.LengthFilter(count)});
        holder.etTitle.setText(getItem(index).getSensorid());
        holder.etTitle.addTextChangedListener(new ProgramFilter(holder.etTitle,count,activity));
        holder.tvNumber.setText(String.valueOf(index+1));
        if(!getItem(index).isShowIndex()){
            holder.tvNumber.setVisibility(View.INVISIBLE);
        }else{
            holder.tvNumber.setVisibility(View.VISIBLE);
        }
        int state = getItem(index).getState();
        if(state == ProgramItemBean.STATE_HIDE){
            holder.ivNormal.setVisibility(View.INVISIBLE);
        }else{
            holder.ivNormal.setVisibility(View.VISIBLE);
        }
        if(state == ProgramItemBean.STATE_NORMAL){
            holder.ivNormal.setImageResource(R.mipmap.iv_square);
        }else if(state == ProgramItemBean.STATE_SUCCESS){
            holder.ivNormal.setImageResource(R.mipmap.iv_square_select);
        }else if(state == ProgramItemBean.STATE_FAILED){
            holder.ivNormal.setImageResource(R.mipmap.iv_check_fail);
        }
        holder.etTitle.setEnabled(getItem(index).isEditable());
        if(getItem(index).isEditable() && index == 0){
            //强制获取焦点
            holder.etTitle.setFocusable(true);
            holder.etTitle.setFocusableInTouchMode(true);
            holder.etTitle.requestFocus();
        }
        holder.itemView.setOnClickListener(v -> {
            int temp = select;
            select = index;
            notifyItemChanged(temp);
            notifyItemChanged(select);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(index, getItem(index));
            }
        });
        holder.etTitle.setClearStatusListener(empty -> {
            getItem(index).setSensorid(holder.etTitle.getText().toString());
        });
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, ProgramItemBean programItemBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_bottom_sep)
        private View vBottomSep;//Sep
        @BindView(R.id.tv_number)
        private TextView tvNumber;//Number
        @BindView(R.id.et_title)
        private ClearEditText etTitle;//Title
        @BindView(R.id.iv_normal)
        private ImageView ivNormal;//Title

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);

        }
    }
}
