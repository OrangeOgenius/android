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
import com.orange.tpms.ue.frag.Frag_base
import com.orange.tpms.ue.frag.Frag_car_model
import com.orange.tpms.ue.frag.Frag_car_year
import com.orange.tpms.ue.kt_frag.Frag_SelectYear
import java.util.ArrayList


class ShowModel(private val models: ArrayList<String>,private val navigationActivity:BleActivity)
    : RecyclerView.Adapter<ShowModel.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_item.text=models[position]
holder.mView.setOnClickListener{
    PublicBean.SelectModel = models.get(position)
    navigationActivity.ChangePage(Frag_SelectYear(),R.id.frage,"Frag_SelectYear",true)
//    navigationActivity.toFrag(Frag_car_year::class.java, false, true, "")
}
    }

    override fun getItemCount(): Int = models.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val text_item: TextView = mView.findViewById(R.id.text_item)
        override fun toString(): String {
            return super.toString() + " '" + text_item.text + "'"
        }
    }
}