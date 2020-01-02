package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Id_Copy : RootFragement(R.layout.fragment_frag__id__copy) {
    var position=0
    var btn=ArrayList<RelativeLayout>()
    override fun viewInit() {
        btn.add(rootview.b1)
        btn.add(rootview.b2)
        btn.add(rootview.b3)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
            JzActivity.getControlInstance().changeFrag(Frag_Scan(),R.id.frage,"Frag_Scan",true)
        }
        rootview.b2.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
        }
        rootview.b3.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Favorite(),R.id.frage,"Frag_Favorite",true)
        }
        rootview.b1.isSelected=true
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
