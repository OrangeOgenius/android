package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import kotlinx.android.synthetic.main.fragment_frag__program__sensor.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Program_Sensor : RootFragement() {
    var position=0
    var btn=ArrayList<RelativeLayout>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(isInitialized()){return rootview}
        rootview=inflater.inflate(R.layout.fragment_frag__program__sensor, container, false)
        btn.add(rootview.b1)
        btn.add(rootview.b2)
        btn.add(rootview.b3)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
            act.ChangePage(Frag_Scan(),R.id.frage,"Frag_Scan",true)
        }
        rootview.b2.setOnClickListener {
            act.ChangePage(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
        }
        rootview.b3.setOnClickListener {
            act.ChangePage(Frag_Favorite(),R.id.frage,"Frag_Favorite",true)
        }
        rootview.b1.isSelected=true
        return rootview
    }
    override fun onTop() {
        ChangePosition(-1)
    }

    override fun onDown() {
        ChangePosition(1)
    }
    fun ChangePosition(a:Int){
        for(i in btn){
            i.isSelected=false
        }
        if(position+a in 0..2){
            position+=a
        }
        btn[position].isSelected=true
    }

    override fun enter() {
        btn[position].performClick()
    }
}
