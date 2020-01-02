package com.orange.tpms.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.helper.WifiConnectHelper;

public class WifiAdapter extends BaseRecyclerAdapter<ScanResult, WifiAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public WifiAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_wifi, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        holder.tvTitle.setText(getItem(index).SSID);
        if(WifiConnectHelper.hasPassword(mContext,getItem(index))){
            holder.ivlock.setVisibility(View.VISIBLE);
        }else{
            holder.ivlock.setVisibility(View.GONE);
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
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, ScanResult content);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        private TextView tvTitle;//Title
        @BindView(R.id.iv_lock)
        private ImageView ivlock;//Lock

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
