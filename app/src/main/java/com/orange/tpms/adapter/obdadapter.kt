package com.orange.tpms.adapter

import android.text.InputFilter
import android.view.View
import com.orange.jzchi.jzframework.JzAdapter
import com.orange.tpms.R
import com.orange.tpms.bean.ObdBeans
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.utils.KeyboardUtil
import kotlinx.android.synthetic.main.item_id_copy_new.view.*

class obdadapter(public val beans: ObdBeans) : JzAdapter(R.layout.item_id_copy_new) {
    override fun sizeInit(): Int {
        return  beans.rowcount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mView.tv_originalid.isEnabled=false
        if (position == 0) {
            holder.mView.tv_newid.filters= arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            holder.mView.tv_position.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_originalid.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_newid.setBackground(holder.mView.context.resources.getDrawable(R.color.color_orange))
            holder.mView.tv_newid.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.tv_check.setBackground(holder.mView.context.resources.getDrawable(R.color.green))
            holder.mView.tv_check.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.iv_check.setVisibility(View.GONE)
            holder.mView.tv_originalid.setTextColor(holder.mView.context.resources.getColor(R.color.color_black))
            holder.mView.tv_position.text = "WH"
            holder.mView.tv_originalid.setText( "Original ID")
            holder.mView.tv_newid.isEnabled=false
            holder.mView.tv_newid.setText("New sensor")
            holder.mView.tv_check.text = "CHK"
            return
        } else {
            if(PublicBean.position==PublicBean.OBD_RELEARM){
                holder.mView.tv_newid.setTextColor(holder.mView.context.resources.getColor(R.color.color_black))
                holder.mView.tv_newid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
                holder.mView.tv_originalid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
                holder.mView.tv_originalid.setTextColor(holder.mView.context.resources.getColor(R.color.color_orange))
            }else{
                holder.mView.tv_newid.setTextColor(holder.mView.context.resources.getColor(R.color.color_orange))
                holder.mView.tv_newid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
                holder.mView.tv_originalid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
                holder.mView.tv_originalid.setTextColor(holder.mView.context.resources.getColor(R.color.color_black))
            }
            holder.mView.tv_position.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_check.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
            holder.mView.tv_check.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.iv_check.setVisibility(View.VISIBLE)
            holder.mView.tv_originalid.setText("")
            holder.mView.tv_newid.isEnabled=beans.CanEdit
            holder.mView.tv_newid.setText("")
            holder.mView.tv_check.text = ""
            holder.mView.iv_check.setVisibility(View.GONE)
            when (position) {
                0 -> {
                    holder.mView.tv_position.text = "WH"
                }
                1 -> {
                    holder.mView.tv_position.text = "LF"
                }
                2 -> {
                    holder.mView.tv_position.text = "RF"
                }
                3 -> {
                    holder.mView.tv_position.text = "RR"
                }
                4 -> {
                    holder.mView.tv_position.text = "LR"
                }
                5 -> {
                    holder.mView.tv_position.text = "SP"
                }
            }
        }
        if (position - 1 < beans.OldSemsor.size) {
            holder.mView.tv_originalid.setText(beans.OldSemsor[position - 1])
            holder.mView.tv_newid.setText(beans.NewSensor[position - 1])
            when (beans.state[position - 1]) {
                ObdBeans.PROGRAM_FALSE -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.mipmap.error)
                }
                ObdBeans.PROGRAM_WAIT -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.color.white)
                }
                ObdBeans.PROGRAM_SUCCESS -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.mipmap.correct)
                    if(PublicBean.position==PublicBean.OBD_RELEARM){
                        holder.mView.tv_originalid.setTextColor(holder.itemView.context.resources.getColor(R.color.color_black));
                    }else{
                        holder.mView.tv_newid.setTextColor(holder.itemView.context.resources.getColor(R.color.color_black));
                    }

                }
            }
        }
        KeyboardUtil.hideEditTextKeyboard(holder.mView.tv_newid)
        holder.mView.tv_newid.filters= arrayOf<InputFilter>(InputFilter.LengthFilter(beans.idcount))
        holder.mView.tv_newid.setClearStatusListener {
            if(!holder.mView.tv_newid.text.toString().contains("New")){ beans.NewSensor[position-1]=holder.mView.tv_newid.text.toString()}
        }
    }

}