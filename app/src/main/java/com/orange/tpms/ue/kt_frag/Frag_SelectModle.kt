package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.activity.MainActivity
import com.orango.electronic.orangetxusb.Adapter.ShowModel
import kotlinx.android.synthetic.main.fragment_frag__select_modle.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectModle : RootFragement() {
    private var modle: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__select_modle, container, false)
        modle = (activity as KtActivity).itemDAO!!.getModel(PublicBean.SelectMake)!!
        rootview.rv_model.layoutManager= LinearLayoutManager(activity)
        rootview.rv_model.adapter=ShowModel(modle,act)
        return rootview
    }


}
