package com.orange.tpms.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.bean.IDCopyDetailBean;

public class IDCopyDetailAdapter extends BaseRecyclerAdapter<IDCopyDetailBean, IDCopyDetailAdapter.ViewHolder> {

    public IDCopyDetailAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_id_copy_new, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        if(index==0){
            holder.tvPosition.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.gray));
            holder.tvOriginalID.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.gray));
            holder.tvNewID.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.color_orange));
            holder.tvNewID.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tvCheck.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.green));
            holder.tvCheck.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.ivCheck.setVisibility(View.GONE);
        }else{
            holder.tvPosition.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.gray));
            holder.tvOriginalID.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.white));
            holder.tvNewID.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.white));
            holder.tvNewID.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_orange));
            holder.tvCheck.setBackground(holder.itemView.getContext().getResources().getDrawable(R.color.white));
            holder.tvCheck.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.ivCheck.setVisibility(View.VISIBLE);
        }
        if(index == getItemCount()-1){
            holder.vBottomSep.setVisibility(View.VISIBLE);
        }
        String checkTitle = getItem(index).getCheckTitle();
        int state = getItem(index).getState();
        if(TextUtils.isEmpty(checkTitle)){
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.ivCheck.setVisibility(View.GONE);
        }
        if(state == IDCopyDetailBean.STATE_NORMAL){
            holder.ivCheck.setImageResource(R.color.white);
        }else if(state == IDCopyDetailBean.STATE_SUCCESS){
            holder.tvNewID.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_black));
            holder.ivCheck.setImageResource(R.mipmap.icon_correct);
        }else if(state == IDCopyDetailBean.STATE_FAILED){
            holder.ivCheck.setImageResource(R.mipmap.error);
        }else if(state == IDCopyDetailBean.STATE_HIDE){
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
        holder.tvNewID.setEnabled(false);
        holder.tvPosition.setText(getItem(index).getPosition());
        holder.tvOriginalID.setText(getItem(index).getOriginalid());
        holder.tvNewID.setText(getItem(index).getNewid());
        holder.tvCheck.setText(checkTitle);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_bottom_sep)
        private View vBottomSep;//Sep
        @BindView(R.id.tv_position)
        private TextView tvPosition;//Position
        @BindView(R.id.tv_originalid)
        private TextView tvOriginalID;//OriginalID
        @BindView(R.id.tv_newid)
        private TextView tvNewID;//NewID
        @BindView(R.id.tv_check)
        private TextView tvCheck;//Check
        @BindView(R.id.iv_check)
        private ImageView ivCheck;//Check

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
