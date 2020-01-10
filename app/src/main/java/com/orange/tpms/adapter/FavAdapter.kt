package com.orange.tpms.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.kt_frag.*
import java.util.*

class FavAdapter(private val name: ArrayList<String>,val go:Boolean)
    : androidx.recyclerview.widget.RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.favorite_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text=name.get(position).replace("☆","/")
        if(go){holder.delete.visibility=View.GONE
            holder.mView.setOnClickListener {
                PublicBean.SelectYear=name[position].split("☆")[2]
                PublicBean.SelectModel=name[position].split("☆")[1]
                PublicBean.SelectMake=name[position].split("☆")[0]
                Log.e("select",PublicBean.SelectMake+PublicBean.SelectModel+PublicBean.SelectYear)
                when(PublicBean.position){
                    PublicBean.檢查傳感器->{
                        JzActivity.getControlInstance().changeFrag(Frag_Check_Sensor_Information(),R.id.frage,"Frag_Check_Sensor_Information",true)
                    }
                    PublicBean.燒錄傳感器->{
                        JzActivity.getControlInstance().changeFrag(Frag_Program_Number_Choice(), R.id.frage,"Frag_Program_Number_Choice",true);
                    }
                    PublicBean.複製傳感器->{
                        JzActivity.getControlInstance().changeFrag(Frag_Idcopy_Detail(), R.id.frage, "Frag_Idcopy_Detail", true)
                    }
                    PublicBean.學碼步驟->{
                        JzActivity.getControlInstance().changeFrag(Frag_Relearm_Detail(), R.id.frage,"Frag_Relearm_Detail",true);
                    }
                    PublicBean.PAD_PROGRAM->{
                        JzActivity.getControlInstance().changeFrag(Frag_Pad_Program_Detail(), R.id.frage,"Frag_Pad_Program_Detail",true);
                    }
                    PublicBean.PAD_COPY->{
                        JzActivity.getControlInstance().changeFrag(Frag_Pad_Keyin(), R.id.frage,"Frag_Pad_Keyin",true);
                    }
                    PublicBean.ID_COPY_OBD->{
                        JzActivity.getControlInstance().changeFrag(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
                    }
                    PublicBean.OBD_RELEARM->{
                        JzActivity.getControlInstance().changeFrag(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
                    }
                }
            }}else{
            holder.delete.visibility=View.VISIBLE
            holder.delete.setOnClickListener {
                name.removeAt(position)
                this.notifyDataSetChanged()
                Log.d("data","$name/${name.size}")
                val profilePreferences =holder.mView.context.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
                profilePreferences.edit().putInt("count",name.size).commit()
                for(i in 0 until name.size){
                    profilePreferences.edit().putString("$i",name[i]).commit()
                }
            }
        }


    }

    override fun getItemCount(): Int = name.size

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val  name:TextView = mView.findViewById(R.id.textView22)
        val delete:ImageView=mView.findViewById(R.id.imageView2)
        override fun toString(): String {
            return super.toString() + " ''"
        }
    }

}