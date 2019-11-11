package com.orango.electronic.orangetxusb.Adapter

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.activity.MainActivity
import com.orange.tpms.ue.frag.*
import com.orange.tpms.ue.kt_frag.Frag_Check_Sensor_Information
import java.util.ArrayList


class ShowYear(private val years: ArrayList<String>, private val navigationActivity: BleActivity)
    : RecyclerView.Adapter<ShowYear.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.text_item.text=years[position]
    holder.mView.setOnClickListener{
        when(PublicBean.position){
            PublicBean.檢查傳感器->{
                PublicBean.SelectYear=years[position]
                navigationActivity.ChangePage(Frag_Check_Sensor_Information(),R.id.frage,"Frag_Check_Sensor_Information",true)
            }
            PublicBean.燒錄傳感器->{
                PublicBean.SelectYear=years[position]
//                navigationActivity.toFrag(Frag_program_number_choice::class.java, false, true, "");
            }
            PublicBean.複製傳感器->{
                PublicBean.SelectYear=years[position]
//                navigationActivity.toFrag(Frag_id_copy_original::class.java, false, true, "");
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
}