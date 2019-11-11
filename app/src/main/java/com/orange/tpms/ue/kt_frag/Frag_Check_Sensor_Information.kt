package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import kotlinx.android.synthetic.main.frag_check_sensor_information.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Check_Sensor_Information : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__check__sensor__information, container, false)
        rootview.b1.setOnClickListener {
act.ChangePage(Frag_Check_Sensor_Read(),R.id.frage,"Frag_Check_Sensor_Read",true)
        }
        rootview.b2.setOnClickListener {

        }
        return rootview
    }


}
