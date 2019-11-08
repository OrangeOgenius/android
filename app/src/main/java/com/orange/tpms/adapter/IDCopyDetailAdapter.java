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
            holder.ivCheck.setImageResource(R.mipmap.iv_square);
        }else if(state == IDCopyDetailBean.STATE_SUCCESS){
            holder.ivCheck.setImageResource(R.mipmap.iv_square_select);
        }else if(state == IDCopyDetailBean.STATE_FAILED){
            holder.ivCheck.setImageResource(R.mipmap.iv_check_fail);
        }else if(state == IDCopyDetailBean.STATE_HIDE){
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
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
