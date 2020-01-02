package com.orango.electronic.orangetxusb.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.kt_frag.Frag_SelectYear
import java.util.*


class ShowModel(private val models: ArrayList<String>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ShowModel.ViewHolder>() {
var focus=0
    lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_item.text=models[position]
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
                PublicBean.SelectModel = models.get(position)
                JzActivity.getControlInstance().changeFrag(Frag_SelectYear(),R.id.frage,"Frag_SelectYear",true)
            }
            true
        }
    }

    override fun getItemCount(): Int = models.size

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val text_item: TextView = mView.findViewById(R.id.text_item)
        override fun toString(): String {
            return super.toString() + " '" + text_item.text + "'"
        }
    }
}