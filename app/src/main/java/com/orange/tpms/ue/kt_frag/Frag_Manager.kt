package com.orange.tpms.ue.kt_frag

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.JzFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orango.electronic.orangetxusb.SettingPagr.Set_Languages

class Frag_Manager :JzFragement(R.layout.activity_kt){
    lateinit var a:TextView
    lateinit var tit:TextView
    lateinit var titlebar:RelativeLayout
    lateinit var back: ImageView
    lateinit var logout: ImageView
    override fun viewInit() {
        tit = rootview.findViewById(R.id.textView12)
        back = rootview.findViewById(R.id.back)
        logout = rootview.findViewById(R.id.logout)
        titlebar = rootview.findViewById(R.id.toolbar)
        back.setOnClickListener {
            JzActivity.getControlInstance().goBack()
        }
        logout.setOnClickListener {

        }
        titlebar.setOnClickListener {
            a.text=""
        }
        PublicBean.MCU_NUMBER=JzActivity.getControlInstance().getPro("Version","no")
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        if (profilePreferences.getString("admin", "nodata").equals("nodata")) {
            Set_Languages.place = 0
            JzActivity.getControlInstance().changeFrag(Set_Languages(), R.id.frage, "Set_Languages", false)
        }else{
            JzActivity.getControlInstance().changeFrag(Frag_home(), R.id.frage, "Frag_home", false)
        }

    }

}