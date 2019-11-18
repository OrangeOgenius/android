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


class Frag_Pad_IdCopy : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
rootview=inflater.inflate(R.layout.fragment_frag__pad__id_copy, container, false)
        rootview.b1.setOnClickListener {
            PublicBean.ScanType = PublicBean.掃描Mmy
            act.GoScanner(Frag_Scan(),10,R.id.frage,"Frag_Scan")
//            act.ChangePage(Frag_Scan(),R.id.frage,"Frag_Scan",true)
        }
        rootview.b2.setOnClickListener {
            act.GoScanner(Frag_SelectMake(),10,R.id.frage,"Frag_SelectMake")
//            act.ChangePage(Frag_SelectMake(),R.id.frage,"Frag_SelectMake",true)
        }
        rootview.b3.setOnClickListener {
            act.GoScanner(Frag_Favorite(),10,R.id.frage,"Frag_Favorite")
//            act.ChangePage(Frag_Favorite(),R.id.frage,"Frag_Favorite",true)
        }
        return rootview
    }


}
