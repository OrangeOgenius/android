package com.orange.tpms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.bean.YTSelectModelBean;

public class YTSelectModelAdapter extends BaseRecyclerAdapter<YTSelectModelBean, YTSelectModelAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public YTSelectModelAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_yt_select_model, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        String name = getItem(index).getName();
        boolean enable = getItem(index).isEnable();
        holder.tvName.setText(name);
        if(enable){
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.color_white));
            holder.itemView.setOnClickListener(v -> {
                int temp = select;
                select = index;
                notifyItemChanged(temp);
                notifyItemChanged(select);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(index, getItem(index));
                }
            });
        }else{
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.color_4DCCCCCE));
            holder.itemView.setOnClickListener(null);
        }
        holder.itemView.setFocusable(true);
        holder.itemView.setBackgroundResource(R.drawable.yt_car_recycler_selecter);
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, YTSelectModelBean selectModelBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        private TextView tvName;//Name
        @BindView(R.id.prl_contaner)
        private PercentRelativeLayout prlContaner;//Container

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
