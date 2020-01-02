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
import com.orange.tpms.bean.InformationBean;

public class InformationAdapter extends BaseRecyclerAdapter<InformationBean, InformationAdapter.ViewHolder> {

    private int select = 0;//默认选中第一个

    private OnItemClickListener onItemClickListener;

    public InformationAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_information, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        holder.tvTitle.setText(getItem(index).getTitle());
        holder.tvInformation.setText(getItem(index).getInformation());
        holder.ivOk.setVisibility(View.INVISIBLE);
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
        void onItemClick(int pos, InformationBean content);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        private TextView tvTitle;//Title
        @BindView(R.id.tv_information)
        private TextView tvInformation;//Information
        @BindView(R.id.iv_ok)
        private ImageView ivOk;//Ok

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
