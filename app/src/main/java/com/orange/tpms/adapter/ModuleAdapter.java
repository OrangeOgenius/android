package com.orange.tpms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;

import bean.mmy.MMyBean;

public class ModuleAdapter extends BaseRecyclerAdapter<MMyBean, ModuleAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public ModuleAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_module, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        MMyBean mMyBean = getItem(index);
        holder.tvModule.setText(String.valueOf(mMyBean.getModel()));
        holder.itemView.setOnClickListener(v -> {
            int temp = select;
            select = index;
            notifyItemChanged(temp);
            notifyItemChanged(select);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(index, getItem(index));
            }
        });
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, MMyBean mMyBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_module)
        private TextView tvModule;//Module

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
