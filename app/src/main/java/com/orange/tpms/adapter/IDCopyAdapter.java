package com.orange.tpms.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.de.rocket.Rocket;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.bean.IDCopyBean;
import com.orange.tpms.utils.KeyboardUtil;
import com.orange.tpms.utils.NumberUtil;
import com.orange.tpms.widget.ClearEditText;

public class IDCopyAdapter extends BaseRecyclerAdapter<IDCopyBean, IDCopyAdapter.ViewHolder> {
int idcount;
public boolean newsensoe=false;
    public IDCopyAdapter(Context context,int idcount) {
        super(context);
        this.idcount=idcount;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_id_copy_original, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        if(newsensoe){holder.etSensorid.setHint(R.string.app_new_sensor);}
        if(index == getItemCount()-1){
            holder.vBottomSep.setVisibility(View.VISIBLE);
        }
        //不显示软键盘
        KeyboardUtil.hideEditTextKeyboard(holder.etSensorid);
        //全部大写
        if(index==0){
            holder.etSensorid.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvPsi.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvTemp.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvBat.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
        }else{
            holder.etSensorid.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.tvPsi.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.tvTemp.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.tvBat.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        holder.etSensorid.setFilters(new InputFilter[] {new InputFilter.LengthFilter(idcount)});
        String batString = getItem(index).getBat();
        if(!TextUtils.isEmpty(batString) && NumberUtil.isNumber(batString)){
            int batLevel = Integer.valueOf(batString);
            if(batLevel >=90){
                holder.ivBat.setImageResource(R.mipmap.yt_full_power);
            }else if(batLevel >= 30){
                holder.ivBat.setImageResource(R.mipmap.yt_icon_battery);
            }else if(batLevel >=10){
                holder.ivBat.setImageResource(R.mipmap.yt_low_power);
            }else{
                holder.ivBat.setImageResource(R.mipmap.yt_no_power);
            }
        }else{
            holder.tvBat.setText(getItem(index).getBat());
        }
        holder.tvPosition.setText(getItem(index).getPosition());
        holder.etSensorid.setText(getItem(index).getSensorid());
        holder.tvPsi.setText(getItem(index).getPsi());
        holder.tvTemp.setText(getItem(index).getTemp());
        holder.etSensorid.setEnabled(getItem(index).isEditable());
        if(getItem(index).isEditable() && index == 1){
            //强制获取焦点
            holder.etSensorid.setFocusable(true);
            holder.etSensorid.setFocusableInTouchMode(true);
            holder.etSensorid.requestFocus();
        }
        holder.etSensorid.setClearStatusListener(empty -> {
            getItem(index).setSensorid(holder.etSensorid.getText().toString().toUpperCase());
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_bottom_sep)
        private View vBottomSep;//Sep
        @BindView(R.id.tv_position)
        private TextView tvPosition;//Position
        @BindView(R.id.et_sensorid)
        private ClearEditText etSensorid;//ID
        @BindView(R.id.tv_psi)
        private TextView tvPsi;//Psi
        @BindView(R.id.tv_temp)
        private TextView tvTemp;//Temp
        @BindView(R.id.tv_bat)
        private TextView tvBat;//Bat
        @BindView(R.id.iv_bat)
        private ImageView ivBat;//Bat

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
