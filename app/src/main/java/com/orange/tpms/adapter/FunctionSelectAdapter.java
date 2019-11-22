package com.orange.tpms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.bean.FunctionSelectBean;
import com.orange.tpms.utils.ImageUtil;

/**
 * 功能选择
 */
public class FunctionSelectAdapter extends BaseRecyclerAdapter<FunctionSelectBean, FunctionSelectAdapter.ViewHolder> {

    private int select;

    public FunctionSelectAdapter(Context context) {
        super(context);
        select = -1;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_function_selection, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        FunctionSelectBean functionSelectBean = getItem(index);
        if(functionSelectBean.isHide()){
            holder.ivIcon.setVisibility(View.INVISIBLE);
            holder.tvName.setVisibility(View.INVISIBLE);
        }else{
            holder.ivIcon.setVisibility(View.VISIBLE);
            holder.tvName.setVisibility(View.VISIBLE);
            holder.ivIcon.setBackgroundResource(functionSelectBean.getBgSelector());
            holder.ivIcon.setSelected(select == index);
            holder.tvName.setText(functionSelectBean.getTitle());
            if(!functionSelectBean.isSelectable()){
                ImageUtil.toBagroundGrey(holder.ivIcon);
            }else{
                holder.itemView.setOnClickListener(v -> {
                    int temp = select;
                    select = index;
                    notifyItemChanged(temp);
                    notifyItemChanged(select);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(index,getItem(index));
                    }
                });
            }
        }
    }

    /* ***************************** 点击事件 ********************************** */

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos,FunctionSelectBean chooseNameBean);
    }

    /* ***************************** ViewHolder ********************************** */

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        private ImageView ivIcon;//图标
        @BindView(R.id.tv_name)
        private TextView tvName;//标题

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}

