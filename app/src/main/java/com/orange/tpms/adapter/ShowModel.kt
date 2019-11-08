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
import com.orange.tpms.ue.frag.Frag_base
import com.orange.tpms.ue.frag.Frag_car_model
import com.orange.tpms.ue.frag.Frag_car_year
import java.util.ArrayList


class ShowModel(private val models: ArrayList<String>,private val navigationActivity:Frag_base)
    : RecyclerView.Adapter<ShowModel.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_item.text=models[position]
holder.mView.setOnClickListener{
    MainActivity.SelectModel = models.get(position)
    navigationActivity.toFrag(Frag_car_year::class.java, false, true, "")
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