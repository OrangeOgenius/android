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

public class NumberChoiceAdapter extends BaseRecyclerAdapter<String, NumberChoiceAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public NumberChoiceAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_number_choice, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        holder.tvNumber.setText(getItem(index));
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
        void onItemClick(int pos, String content);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_number)
        private TextView tvNumber;//Number

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
