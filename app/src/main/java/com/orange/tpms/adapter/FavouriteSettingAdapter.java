package com.orange.tpms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;

import bean.mmy.MMyBean;

public class FavouriteSettingAdapter extends BaseRecyclerAdapter<MMyBean, FavouriteSettingAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;
    private OnItemDeleteListener onItemDeleteListener;
    private OnItemAddListener onItemAddListener;

    public FavouriteSettingAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public void setOnItemAddListener(OnItemAddListener onItemAddListener) {
        this.onItemAddListener = onItemAddListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_setting_favourite, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        String make = getItem(index).getMake();
        String module = getItem(index).getModel();
        String year = getItem(index).getYear();
        if(TextUtils.isEmpty(make)){
            holder.ivAdddelete.setSelected(false);
            holder.tvName.setText("");
        }else{
            holder.ivAdddelete.setSelected(true);
            holder.tvName.setText(String.valueOf(make+"/"+module+"/"+year));
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
        holder.ivAdddelete.setOnClickListener( view -> {
             if(TextUtils.isEmpty(make)){
                 if (onItemAddListener != null) {
                     onItemAddListener.onItemAdd(index, getItem(index));
                 }
             }else{
                 if (onItemDeleteListener != null) {
                     onItemDeleteListener.onItemDelete(index, getItem(index));
                 }
             }
        });
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, MMyBean mMyBean);
    }

    /**
     * 删除回调接口
     */
    public interface OnItemDeleteListener {
        void onItemDelete(int pos, MMyBean mMyBean);
    }

    /**
     * 删除回调接口
     */
    public interface OnItemAddListener {
        void onItemAdd(int pos, MMyBean mMyBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        private TextView tvName;//Name
        @BindView(R.id.iv_add_delete)
        private ImageView ivAdddelete;//AddDelete

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
