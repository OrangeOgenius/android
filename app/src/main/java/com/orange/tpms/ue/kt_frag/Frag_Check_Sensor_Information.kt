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
var focus=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(isInitialized()){return rootview}
        rootview=inflater.inflate(R.layout.fragment_frag__check__sensor__information, container, false)
        rootview.b1.isSelected=true
        rootview.b1.setOnClickListener {
           act.ChangePage(Frag_Check_Sensor_Read(),R.id.frage,"Frag_Check_Sensor_Read",true)
        }
        rootview.b2.setOnClickListener {
            act.ChangePage(Frag_Check_Location(),R.id.frage,"Frag_Check_Location",true)
        }
        return rootview
    }

    override fun onDown() {
        ChangeFocus(1)
    }

    override fun onTop() {
        ChangeFocus(-1)
    }
    fun ChangeFocus(a:Int){
        if(focus+a>=0&&focus+a<2){focus+=a;}
        rootview.b1.isSelected=false
        rootview.b2.isSelected=false
        if(focus==0){
            rootview.b1.isSelected=true
        }else{
            rootview.b2.isSelected=true
        }
    }
    override fun enter(){
        if(focus==0){
            rootview.b1.performClick()
        }else{
            rootview.b2.performClick()
        }
    }
}
