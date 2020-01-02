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
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.R;
import com.orange.tpms.bean.YTReadSensorBean;

public class YTReadSensorAdapter extends BaseRecyclerAdapter<YTReadSensorBean, YTReadSensorAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;

    public YTReadSensorAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new ViewHolder(inflate(R.layout.item_yt_read_sensor_title, parent));
    }

    @Override
    protected void onBindView(ViewHolder holder, int index) {
        if(index != getItemCount() - 1){
            holder.vBottom.setVisibility(View.GONE);
        }else{
            holder.vBottom.setVisibility(View.VISIBLE);
        }
        boolean isTitle = getItem(index).isTitle();
        int batLevel = getItem(index).getBatLevel();
        String name = getItem(index).getName();
        String tirePress = getItem(index).getTirePress();
        String tireTemp = getItem(index).getTireTemp();
        String idNumber = getItem(index).getIdNumber();
        String batName = getItem(index).getBatName();

        setTextStatus(holder.tvName,name,isTitle);
        setTextStatus(holder.tvTirePress,tirePress,isTitle);
        setTextStatus(holder.tvTireTemp,tireTemp,isTitle);
        setTextStatus(holder.tvID,idNumber,isTitle);
        setTextStatus(holder.tvBat,batName,isTitle);

        if(isTitle){
            holder.prlContaner.setBackgroundResource(R.color.color_FF3C80B5);
            holder.tvBat.setVisibility(View.VISIBLE);
            holder.ivBat.setVisibility(View.GONE);
        }else{
            holder.prlContaner.setBackgroundResource(R.color.color_transparent);
            holder.tvIndex.setText(String.valueOf(index));
            if(batLevel < 0 ){
                holder.tvBat.setVisibility(View.VISIBLE);
                holder.ivBat.setVisibility(View.GONE);
                setTextStatus(holder.tvBat,batName,false);
            }else{
                holder.tvBat.setVisibility(View.GONE);
                holder.ivBat.setVisibility(View.VISIBLE);
                if(batLevel >=90){
                    holder.ivBat.setImageResource(R.mipmap.yt_full_power);
                }else if(batLevel >= 30){
                    holder.ivBat.setImageResource(R.mipmap.yt_icon_battery);
                }else if(batLevel >=10){
                    holder.ivBat.setImageResource(R.mipmap.yt_low_power);
                }else{
                    holder.ivBat.setImageResource(R.mipmap.yt_no_power);
                }
            }
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(index, getItem(index));
                }
            });
            holder.itemView.setFocusable(true);
            holder.itemView.setBackgroundResource(R.drawable.yt_car_recycler_selecter);
        }
    }

    /**
     * 设置文本状态
     */
    private void setTextStatus(TextView textView,String content,boolean isTitle){
        if(isTitle){
            textView.setText(content);
            textView.setTextColor(mContext.getResources().getColor(R.color.color_white));
        }else{
            if(content == null){
                textView.setText("----");
                textView.setTextColor(mContext.getResources().getColor(R.color.color_red));
            }else{
                textView.setText(content);
                textView.setTextColor(mContext.getResources().getColor(R.color.color_FF3C80B5));
            }
        }
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int pos, YTReadSensorBean readSensorBean);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.prl_container)
        private PercentRelativeLayout prlContaner;
        @BindView(R.id.tv_index)
        private TextView tvIndex;
        @BindView(R.id.tv_name)
        private TextView tvName;
        @BindView(R.id.tv_id)
        private TextView tvID;
        @BindView(R.id.tv_tire_press)
        private TextView tvTirePress;
        @BindView(R.id.tv_tire_temp)
        private TextView tvTireTemp;
        @BindView(R.id.tv_bat)
        private TextView tvBat;
        @BindView(R.id.iv_bat)
        private ImageView ivBat;
        @BindView(R.id.v_bottom)
        private View vBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Rocket.bindViewHolder(this,itemView);
        }
    }
}
