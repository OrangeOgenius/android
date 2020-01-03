package com.orange.tpms.ue.kt_frag


import android.widget.RelativeLayout
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__obd.view.*

class Frag_Obd : RootFragement(R.layout.fragment_frag__obd) {
    override fun viewInit() {
        btn.add(rootview.b1)
        btn.add(rootview.b2)
        btn.add(rootview.b3)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
            (JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager.scan(Frag_Scan(),"Frag_Scan")
        }
        rootview.b2.setOnClickListener {
            (JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager.scan(Frag_SelectMake(),"Frag_SelectMake")
        }
        rootview.b3.setOnClickListener {
            (JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager.scan(Frag_Favorite(),"Frag_Favorite")
        }
        rootview.b1.isSelected=true
    }

    var position=0
    var btn=ArrayList<RelativeLayout>()

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
