package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.jzchi.jzframework.JzActivity
import com.orange.testlauncher.Frage.HomeScreem
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_frag__enginer.view.*

class Frag_Enginer : RootFragement(R.layout.fragment_frag__enginer) {
    override fun viewInit() {
        rootview.connect.setOnClickListener {
            if(rootview.pass.text.toString()=="orangetpms"){
                JzActivity.getControlInstance().changeFrag(HomeScreem(),R.id.frage,"HomeScreem",true)
            }else{
                JzActivity.getControlInstance().toast(resources.getString(R.string.errorpass))
            }
        }
    }

}
