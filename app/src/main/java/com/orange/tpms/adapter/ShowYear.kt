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
import com.orange.tpms.R
import com.orange.tpms.ue.activity.MainActivity
import com.orange.tpms.ue.frag.*
import java.util.ArrayList


class ShowYear(private val years: ArrayList<String>, private val navigationActivity: Frag_base)
    : RecyclerView.Adapter<ShowYear.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.text_item.text=years[position]
    holder.mView.setOnClickListener{
        when(MainActivity.position){
            MainActivity.檢查傳感器->{
                MainActivity.SelectYear=years[position]
                navigationActivity.toFrag(Frag_check_sensor_information::class.java, false, true, "");
            }
            MainActivity.燒錄傳感器->{
                MainActivity.SelectYear=years[position]
                navigationActivity.toFrag(Frag_program_number_choice::class.java, false, true, "");
            }
            MainActivity.複製傳感器->{
                MainActivity.SelectYear=years[position]
                navigationActivity.toFrag(Frag_id_copy_original::class.java, false, true, "");
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