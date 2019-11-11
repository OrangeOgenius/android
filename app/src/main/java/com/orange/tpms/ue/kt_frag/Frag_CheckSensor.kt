package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.bean.ScanQrCodeBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_frag__check_sensor.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_CheckSensor : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.fragment_frag__check_sensor, container, false)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = ScanQrCodeBean.TYPE_MMY
        }
        rootview.b2.setOnClickListener {
act.ChangePage(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
        }
        rootview.b3.setOnClickListener {

        }
        return rootview
    }


}
