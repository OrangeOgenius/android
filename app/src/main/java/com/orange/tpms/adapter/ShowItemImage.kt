package com.orange.tpms.adapter


import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.Item
import com.orange.tpms.ue.kt_frag.Frag_SelectModle

import java.util.ArrayList


class ShowItemImage(var makes: ArrayList<Item>) : RecyclerView.Adapter<ShowItemImage.ViewHolder>() {
    var focus = 0


    internal var a = true

    private val handler = object : Handler() {

        override fun handleMessage(msg: Message) {

            super.handleMessage(msg)

            when (msg.what) {

            }

        }

    }


    // 建立ViewHolder

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val simpleDraweeView: SimpleDraweeView

        init {
            simpleDraweeView = itemView.findViewById(R.id.make_item)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.make_grid_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mipmapId = holder.itemView.context.resources.getIdentifier(
            makes[position].makeImg,
            "mipmap",
            holder.itemView.context.packageName
        )
        Glide.with(holder.itemView.context).load(mipmapId)
            .centerCrop().into(holder.simpleDraweeView)
        if (position == focus) {

            holder.simpleDraweeView.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.drawable.bg_32414e_r8)
        } else {
            holder.simpleDraweeView.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.color.white)
        }
        holder.simpleDraweeView.setOnTouchListener { v, event ->
            Log.e("event", event.toString())
            if (event.action == MotionEvent.ACTION_MOVE) {
                holder.simpleDraweeView.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.drawable.bg_32414e_r8)
            } else {
                holder.simpleDraweeView.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.color.white)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                PublicBean.SelectMake = makes[position].make
                JzActivity.getControlInstance().changeFrag(Frag_SelectModle(), R.id.frage, "Frag_SelectModle", true);
            }
            true
        }
    }


    //    }


    override fun getItemCount(): Int {

        return makes.size

    }

}