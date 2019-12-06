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
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.*
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.b1
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.b2
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.b3
import kotlinx.android.synthetic.main.fragment_frag__program__sensor.view.*

class Frag_Obd : RootFragement() {
    var position=0
    var btn=ArrayList<RelativeLayout>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(isInitialized()){return rootview}
        rootview=inflater.inflate(R.layout.fragment_frag__obd, container, false)
        btn.add(rootview.b1)
        btn.add(rootview.b2)
        btn.add(rootview.b3)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
//            act.ChangePage(Frag_Scan(),R.id.frage,"Frag_Scan",true)
            act.GoScanner(Frag_Scan(),10,R.id.frage,"Frag_Scan")
        }
        rootview.b2.setOnClickListener {
//            act.ChangePage(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
            act.GoScanner(Frag_SelectMake(),10,R.id.frage,"Frag_SelectMake")
        }
        rootview.b3.setOnClickListener {
//            act.ChangePage(Frag_Favorite(),R.id.frage,"Frag_Favorite",true)
            act.GoScanner(Frag_Favorite(),10,R.id.frage,"Frag_Favorite")
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
