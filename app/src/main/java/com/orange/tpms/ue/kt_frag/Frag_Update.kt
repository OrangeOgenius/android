package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__update.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Update : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__update, container, false)
        rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
        rootview.iv_check.setOnClickListener {
            if(GetPro("AutoUpdate",true)){SetPro("AutoUpdate",false)}else{SetPro("AutoUpdate",true)}
            rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
            }

        return rootview
    }


}
