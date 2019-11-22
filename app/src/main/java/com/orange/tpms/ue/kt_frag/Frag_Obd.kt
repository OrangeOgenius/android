package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.*

class Frag_Obd : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__obd, container, false)
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
        return rootview
    }


}
