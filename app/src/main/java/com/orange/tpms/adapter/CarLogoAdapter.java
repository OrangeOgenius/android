package com.orange.tpms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.bean.MMYItemBean;
import com.orange.tpms.utils.SelectorUtils;

public class CarLogoAdapter extends BaseRecyclerAdapter<MMYItemBean, CarLogoAdapter.ViewHolder> {

    private int select;

    private OnItemClickListener onItemClickListener;

    public CarLogoAdapter(Context context) {
        super(context);
        select = -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_car_logo, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        MMYItemBean mmyItemBean = getItem(index);
        SelectorUtils.setSelectorFromAssets(mContext,mmyItemBean.getNormalPath(),mmyItemBean.getSelectPath(),holder.ivLogo);
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
        void onItemClick(int pos, MMYItemBean mmyItemBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_car_logo)
        private ImageView ivLogo;//Logo

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
