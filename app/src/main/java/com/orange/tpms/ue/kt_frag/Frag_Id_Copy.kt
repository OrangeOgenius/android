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
import kotlinx.android.synthetic.main.fragment_frag__id__copy.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Id_Copy : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.fragment_frag__id__copy, container, false)
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
        return rootview
    }


}
