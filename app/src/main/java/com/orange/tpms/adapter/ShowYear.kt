package com.orange.tpms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.kt_frag.*
import java.util.*


class ShowYear(private val years: ArrayList<String>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ShowYear.ViewHolder>() {
    var focus=0
    var favorite= ArrayList<String>()
    lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.text_item.text=years[position]
        if (position == focus) {
            holder.mView.background = context.getDrawable(R.color.color_orange);
        } else {
            holder.mView.background = context.getDrawable(R.color.color_unite_bg);
        }
        holder.mView.setOnTouchListener { v, event ->
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                holder.mView.background = context.getDrawable(R.color.color_orange);
            }else{
                holder.mView.background = context.getDrawable(R.color.color_unite_bg);
            }
            if(event.action == MotionEvent.ACTION_UP){
                PublicBean.SelectYear=years[position]
                AddFavorite()
                ChangePage()
            }
            true
        }
    }
fun ChangePage(){
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
}
    override fun getItemCount(): Int = years.size

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val text_item: TextView = mView.findViewById(R.id.text_item)
        override fun toString(): String {
            return super.toString() + " '" + text_item.text + "'"
        }
    }
    fun AddFavorite(){
        GetFav()
        if(!favorite.contains("${PublicBean.SelectMake}☆${PublicBean.SelectModel}☆${PublicBean.SelectYear}")){ favorite.add("${PublicBean.SelectMake}☆${PublicBean.SelectModel}☆${PublicBean.SelectYear}")}
        SetFav()
    }
    fun GetFav(){
        favorite.clear()
        val profilePreferences = JzActivity.getControlInstance().getRootActivity().getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("$i","nodata")
            if (!tmpdata.equals("nodata")){   favorite.add(tmpdata)}
        }
    }
    fun SetFav(){
        val profilePreferences = JzActivity.getControlInstance().getRootActivity().getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("count",favorite.size).commit()
        for(i in 0 until favorite.size){
            profilePreferences.edit().putString("$i",favorite[i]).commit()
        }
    }
}