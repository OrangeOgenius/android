package com.orange.tpms.adapter;


import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orange.blelibrary.blelibrary.BleActivity;
import com.orange.tpms.R;
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.mmySql.Item;
import com.orange.tpms.ue.kt_frag.Frag_SelectModle;

import java.util.ArrayList;


public class ShowItemImage extends RecyclerView.Adapter<ShowItemImage.ViewHolder> {
    public ArrayList<Item> makes ;
    public BleActivity navigationActivity;
    public ShowItemImage(BleActivity navigationActivity, ArrayList<Item> makes) {
this.navigationActivity=navigationActivity;
this.makes=makes;
    }



    // 建立ViewHolder

    class ViewHolder extends RecyclerView.ViewHolder{
public final SimpleDraweeView simpleDraweeView;
        ViewHolder(final View itemView) {
            super(itemView);
simpleDraweeView=itemView.findViewById(R.id.make_item);
        }
    }





    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.make_grid_item, parent, false);
        return new ViewHolder(view);

    }



    boolean a=true;

    @Override

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int mipmapId=holder.itemView.getContext().getResources().getIdentifier(makes.get(position).getMakeImg(),"mipmap",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext()).load(mipmapId)
   .centerCrop().into(holder.simpleDraweeView);
        holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicBean.SelectMake=(makes.get(position).getMake());
                navigationActivity.ChangePage(new Frag_SelectModle(),R.id.frage,"Frag_car_model",true);
//                navigationActivity.toFrag(Frag_car_model.class,false,true,"");
            }
        });
    }





//    }





    @Override

    public int getItemCount() {

        return makes.size();

    }

    private Handler handler=new Handler(){

        @Override

        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what){



            }

        }

    };

}