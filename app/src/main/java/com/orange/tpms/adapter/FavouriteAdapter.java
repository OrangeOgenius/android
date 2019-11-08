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

public class FavouriteAdapter extends BaseRecyclerAdapter<MMyBean, FavouriteAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public FavouriteAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_favourite, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        String make = getItem(index).getMake();
        String module = getItem(index).getModel();
        String year = getItem(index).getYear();
        holder.tvName.setText(String.valueOf(make+"/"+module+"/"+year));
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

        @BindView(R.id.tv_name)
        private TextView tvName;//Name

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
