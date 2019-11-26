package com.orange.tpms.adapter

import android.view.View
import com.orange.blelibrary.blelibrary.Adapter.RootAdapter
import com.orange.tpms.R
import com.orange.tpms.bean.ObdBeans
import com.orange.tpms.bean.PublicBean
import kotlinx.android.synthetic.main.item_id_copy_new.view.*

class obdadapter(val beans: ObdBeans) : RootAdapter(R.layout.item_id_copy_new) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.mView.tv_position.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_originalid.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_newid.setBackground(holder.mView.context.resources.getDrawable(R.color.color_orange))
            holder.mView.tv_newid.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.tv_check.setBackground(holder.mView.context.resources.getDrawable(R.color.green))
            holder.mView.tv_check.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.iv_check.setVisibility(View.GONE)
            holder.mView.tv_position.text = "WH"
            holder.mView.tv_originalid.text = "Original ID"
            holder.mView.tv_newid.text = "New sensor"
            holder.mView.tv_check.text = "CHK"
            return
        } else {
            holder.mView.tv_position.setBackground(holder.mView.context.resources.getDrawable(R.color.gray))
            holder.mView.tv_originalid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
            holder.mView.tv_newid.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
            holder.mView.tv_newid.setTextColor(holder.mView.context.resources.getColor(R.color.color_orange))
            holder.mView.tv_check.setBackground(holder.mView.context.resources.getDrawable(R.color.white))
            holder.mView.tv_check.setTextColor(holder.mView.context.resources.getColor(R.color.white))
            holder.mView.iv_check.setVisibility(View.VISIBLE)
            holder.mView.tv_originalid.text = ""
            holder.mView.tv_newid.text = ""
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
            holder.mView.tv_originalid.text = beans.OldSemsor[position - 1]
            holder.mView.tv_newid.text = beans.NewSensor[position - 1]
            when (beans.state[position - 1]) {
                ObdBeans.PROGRAM_FALSE -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.mipmap.iv_check_fail)
                }
                ObdBeans.PROGRAM_WAIT -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.mipmap.iv_square)
                }
                ObdBeans.PROGRAM_SUCCESS -> {
                    holder.mView.iv_check.setVisibility(View.VISIBLE)
                    holder.mView.iv_check.setImageResource(R.mipmap.iv_square_select)
                    holder.mView.tv_newid.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color_black));
                }
            }
        }
    }

    override fun getItemCount(): Int = beans.rowcount
}