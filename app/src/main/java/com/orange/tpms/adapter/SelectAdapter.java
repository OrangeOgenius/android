package com.orange.tpms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;

public class SelectAdapter extends BaseRecyclerAdapter<String, SelectAdapter.ViewHolder> {

    private int select = 0;//默认选中第一个

    private OnItemClickListener onItemClickListener;

    public SelectAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_select, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        if(index == select){
            holder.ivOk.setVisibility(View.VISIBLE);
        }else{
            holder.ivOk.setVisibility(View.INVISIBLE);
        }
        holder.tvTitle.setText(getItem(index));
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
     * 设置选中项
     * @param select 选中项
     */
    public void setSelect(int select){
        this.select = select;
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, String content);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        private TextView tvTitle;//Title
        @BindView(R.id.iv_ok)
        private ImageView ivOk;//Ok

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
