package com.orange.tpms.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.kt_frag.*
import java.util.*


class ShowYear(private val years: ArrayList<String>, private val navigationActivity: BleActivity)
    :RecyclerView.Adapter<ShowYear.ViewHolder>() {
    var favorite= ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.text_item.text=years[position]
    holder.mView.setOnClickListener{
        AddFavorite()
        PublicBean.SelectYear=years[position]
        when(PublicBean.position){
            PublicBean.檢查傳感器->{
                navigationActivity.ChangePage(Frag_Check_Sensor_Information(),R.id.frage,"Frag_Check_Sensor_Information",true)
            }
            PublicBean.燒錄傳感器->{
                navigationActivity.ChangePage(Frag_Program_Number_Choice(), R.id.frage,"Frag_Program_Number_Choice",true);
            }
            PublicBean.複製傳感器->{
                navigationActivity.ChangePage(Frag_Idcopy_original(), R.id.frage,"Frag_Idcopy_original",true);
            }
            PublicBean.學碼步驟->{
                navigationActivity.ChangePage(Frag_Relearm_Detail(), R.id.frage,"Frag_Relearm_Detail",true);
            }
            PublicBean.PAD_PROGRAM->{
                navigationActivity.ChangePage(Frag_Pad_Program_Detail(), R.id.frage,"Frag_Pad_Program_Detail",true);
            }
            PublicBean.PAD_COPY->{
                navigationActivity.ChangePage(Frag_Pad_Keyin(), R.id.frage,"Frag_Pad_Keyin",true);
            }
            PublicBean.ID_COPY_OBD->{
                navigationActivity.ChangePage(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
            }
            PublicBean.OBD_RELEARM->{
                navigationActivity.ChangePage(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
            }
        }

}
    }

    override fun getItemCount(): Int = years.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
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
        val profilePreferences = navigationActivity.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("$i","nodata")
            if (!tmpdata.equals("nodata")){   favorite.add(tmpdata)}
        }
    }
    fun SetFav(){
        val profilePreferences = navigationActivity.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("count",favorite.size).commit()
        for(i in 0 until favorite.size){
            profilePreferences.edit().putString("$i",favorite[i]).commit()
        }
    }
}