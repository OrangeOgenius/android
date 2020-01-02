package com.orange.tpms.ue.kt_frag


import android.widget.RelativeLayout
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import kotlinx.android.synthetic.main.fragment_frag__pad__id_copy.view.*


class Frag_Pad_IdCopy : RootFragement(R.layout.fragment_frag__pad__id_copy) {
    override fun viewInit() {
        btn.add(rootview.b1)
        btn.add(rootview.b2)
        btn.add(rootview.b3)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
//            act.GoScanner(Frag_Scan(),10,R.id.frage,"Frag_Scan")
//            JzActivity.getControlInstance().changeFrag(Frag_Scan(),R.id.frage,"Frag_Scan",true)
        }
        rootview.b2.setOnClickListener {
//            act.GoScanner(Frag_SelectMake(),10,R.id.frage,"Frag_SelectMake")
//            JzActivity.getControlInstance().changeFrag(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
        }
        rootview.b3.setOnClickListener {
//            act.GoScanner(Frag_Favorite(),10,R.id.frage,"Frag_Favorite")
//            JzActivity.getControlInstance().changeFrag(Frag_Favorite(),R.id.frage,"Frag_Favorite",true)
        }
        rootview.b1.isSelected=true
    }

    var position = 0
    var btn = ArrayList<RelativeLayout>()

    override fun onTop() {
        ChangePosition(-1)
    }

    override fun onDown() {
        ChangePosition(1)
    }

    fun ChangePosition(a: Int) {
        for (i in btn) {
            i.isSelected = false
        }
        if (position + a in 0..2) {
            position += a
        }
        btn[position].isSelected = true
    }

    override fun enter() {
        btn[position].performClick()
    }


}
